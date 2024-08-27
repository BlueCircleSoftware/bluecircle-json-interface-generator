/*
 * Copyright 2018 Blue Circle Software, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bluecirclesoft.open.jigen.typescript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.model.JAny;
import com.bluecirclesoft.open.jigen.model.JArray;
import com.bluecirclesoft.open.jigen.model.JBoolean;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JMap;
import com.bluecirclesoft.open.jigen.model.JNull;
import com.bluecirclesoft.open.jigen.model.JNumber;
import com.bluecirclesoft.open.jigen.model.JObject;
import com.bluecirclesoft.open.jigen.model.JSpecialization;
import com.bluecirclesoft.open.jigen.model.JString;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.bluecirclesoft.open.jigen.model.JTypeVisitorVoid;
import com.bluecirclesoft.open.jigen.model.JUnionType;
import com.bluecirclesoft.open.jigen.model.JVoid;
import com.bluecirclesoft.open.jigen.model.JWildcard;
import com.bluecirclesoft.open.jigen.model.Namespace;

/**
 * TODO document me
 */
class AccessorProducer implements JTypeVisitorVoid {

	private static final Logger log = LoggerFactory.getLogger(AccessorProducer.class);

	private final String name;

	private final TSFileWriter writer;

	private final Options options;

	private final UnknownProducer unknownProducer;

	private final String immutableSuffix;

	private final TypeUsageProducer usageProducerNoSuffix;

	private final TypeUsageProducer usageProducerSpecializedNoSuffix;

	private final TypeUsageProducer usageProducerWithSuffix;

	private final Namespace currentNamespace;

	AccessorProducer(String name, TSFileWriter writer, Options options, Namespace currentNamespace) {
		this.name = name;
		this.writer = writer;
		this.unknownProducer = new UnknownProducer(options);
		this.immutableSuffix = options.getImmutableSuffix();
		this.options = options;
		this.currentNamespace = currentNamespace;
		usageProducerNoSuffix = new TypeUsageProducer(options, TypeUsageProducer.UseImmutableSuffix.NO);
		usageProducerSpecializedNoSuffix =
				new TypeUsageProducer(options, TypeUsageProducer.WillBeSpecialized.YES, TypeUsageProducer.UseImmutableSuffix.NO);
		usageProducerWithSuffix = new TypeUsageProducer(options, TypeUsageProducer.UseImmutableSuffix.YES);
	}

	public static String capitalize(String variableName) {
		return variableName.substring(0, 1).toUpperCase() + variableName.substring(1);
	}

	private String writeTypeVariables(JType jType, TypeUsageProducer tup) {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		boolean needsComma = false;
		for (JType param : jType.getTypeVariables()) {
			if (needsComma) {
				sb.append(", ");
			} else {
				needsComma = true;
			}
			sb.append(param.accept(tup.getProducer(currentNamespace, writer)));
		}
		sb.append(">");
		return sb.toString();
	}

	private String writeSpecializedTypes(JSpecialization jSpecialization, TypeUsageProducer tup) {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		boolean needsComma = false;
		for (JType param : jSpecialization.getParameters()) {
			if (needsComma) {
				sb.append(", ");
			} else {
				needsComma = true;
			}
			sb.append(param.accept(tup.getProducer(currentNamespace, writer)));
		}
		sb.append(">");
		return sb.toString();
	}

	private void primitiveAccessor(String type) {
		writer.line("public get " + name + "() : Readonly<" + type + "> {");
		writer.indentIn();
		writer.line("return this._delegate.getSub(\"" + name + "\") as Readonly<" + type + ">;");
		writer.indentOut();
		writer.line("}");
		writer.line("public set " + name + "(v : Readonly<" + type + ">) {");
		writer.indentIn();
		writer.line("this._delegate.setSub(\"" + name + "\", v);");
		writer.indentOut();
		writer.line("}");
	}

	private void objectAccessor(String type, String variables) {
		writer.line("public get " + name + "() : " + type + immutableSuffix + variables + " {");
		writer.indentIn();
		writer.line("return new " + type + immutableSuffix + variables + "(this._delegate.root, this._delegate.extend(\"" + name + "\"));");
		writer.indentOut();
		writer.line("}");
	}

	private void primitiveArrayAccessor(String type) {
		writer.addImport("jsonInterfaceGenerator", currentNamespace, writer.getJIGNamespace());
		writer.line("public get " + name + "() : jsonInterfaceGenerator.PrimitiveArrayWrapper<" + type + "> {");
		writer.indentIn();
		writer.line("return new jsonInterfaceGenerator.PrimitiveArrayWrapper<" + type + ">(this._delegate.root, this._delegate.extend(\"" +
				name + "\"));");
		writer.indentOut();
		writer.line("}");
	}

	private void objectArrayAccessor(String intfType, String wrapperType) {
		writer.addImport("jsonInterfaceGenerator", currentNamespace, writer.getJIGNamespace());
		writer.line("public get " + name + "(): jsonInterfaceGenerator.PrimitiveArrayWrapper<" + intfType + "> {");
		writer.indentIn();
		writer.line(
				"return new jsonInterfaceGenerator.PrimitiveArrayWrapper<" + intfType + ">(this._delegate.root, this._delegate.extend(\"" +
						name + "\"));");
		writer.indentOut();
		writer.line("}");
	}

	@Override
	public void visit(JObject jObject) {
		if (jObject.isConstructible()) {
			objectAccessor(jObject.accept(usageProducerNoSuffix.getProducer(currentNamespace, writer)), "");
		}
	}

	@Override
	public void visit(JAny jAny) {
		primitiveAccessor(unknownProducer.getUnknown());
	}

	@Override
	public void visit(JArray jArray) {
		String interfaceName = jArray.getElementType().accept(usageProducerNoSuffix.getProducer(currentNamespace, writer));
		if (jArray.getElementType().needsWrapping()) {
			if (jArray.getElementType() instanceof JTypeVariable) {
				primitiveArrayAccessor(interfaceName);
			} else {
				String wrapperName = jArray.getElementType().accept(usageProducerWithSuffix.getProducer(currentNamespace, writer));
				objectArrayAccessor(interfaceName, wrapperName);
			}
		} else {
			primitiveArrayAccessor(interfaceName);
		}
	}

	@Override
	public void visit(JBoolean jBoolean) {
		primitiveAccessor("boolean");
	}

	@Override
	public void visit(JEnum jEnum) {
		primitiveAccessor(jEnum.accept(usageProducerNoSuffix.getProducer(currentNamespace, writer)));
	}

	@Override
	public void visit(JNumber jNumber) {
		primitiveAccessor("number");
	}

	@Override
	public void visit(JString jString) {
		primitiveAccessor("string");
	}

	@Override
	public void visit(JVoid jVoid) {
	}

	@Override
	public void visit(JSpecialization jSpecialization) {
		TypeUsageProducer tup = usageProducerSpecializedNoSuffix;
		String type =
				jSpecialization.getBase().accept(tup.getProducer(currentNamespace, writer)) + writeSpecializedTypes(jSpecialization, tup);
		primitiveAccessor(type);
	}

	@Override
	public void visit(JTypeVariable jTypeVariable) {
		primitiveAccessor(jTypeVariable.getName());
	}

	@Override
	public void visit(JMap jMap) {
		primitiveAccessor(
				"{[name: string]:" + jMap.getValueType().accept(usageProducerNoSuffix.getProducer(currentNamespace, writer)) + "}");
	}

	@Override
	public void visit(JUnionType jUnionType) {
		JType stripped = jUnionType.getStripped();
		if (stripped.needsWrapping()) {
			if (stripped instanceof JArray) {
				visit((JArray) stripped);
				return;
			} else if (stripped instanceof JObject) {
				if (stripped.hasTypeVariables()) {
					String typeString = stripped.accept(usageProducerSpecializedNoSuffix.getProducer(currentNamespace, writer));
					String variables = writeTypeVariables(stripped, usageProducerNoSuffix);
					objectAccessor(typeString, variables);
					return;
				} else {
					String typeString = stripped.accept(usageProducerNoSuffix.getProducer(currentNamespace, writer));
					objectAccessor(typeString, "");
					return;
				}
			} else if (stripped instanceof JSpecialization) {
				visit((JSpecialization) stripped);
				return;
			}
		}
		String typeString = jUnionType.accept(usageProducerNoSuffix.getProducer(currentNamespace, writer));
		primitiveAccessor(typeString);
	}

	@Override
	public void visit(JNull jNull) {
		primitiveAccessor(jNull.accept(usageProducerNoSuffix.getProducer(currentNamespace, writer)));
	}

	@Override
	public void visit(JWildcard jWildcard) {
		primitiveAccessor(jWildcard.accept(usageProducerNoSuffix.getProducer(currentNamespace, writer)));
	}
}

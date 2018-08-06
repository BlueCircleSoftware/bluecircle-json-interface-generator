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
import com.bluecirclesoft.open.jigen.model.JTypeVisitor;
import com.bluecirclesoft.open.jigen.model.JUnionType;
import com.bluecirclesoft.open.jigen.model.JVoid;
import com.bluecirclesoft.open.jigen.model.JWildcard;

/**
 * TODO document me
 */
class AccessorProducer implements JTypeVisitor<Object> {

	private static final Logger log = LoggerFactory.getLogger(AccessorProducer.class);

	private final String name;

	private final OutputHandler writer;

	private final boolean treatNullAsUndefined;

	private final boolean useUnknown;

	public String capitalize(String variableName) {
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
			sb.append(param.accept(tup));
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
			sb.append(param.accept(tup));
		}
		sb.append(">");
		return sb.toString();
	}

	public AccessorProducer(String name, OutputHandler writer, boolean treatNullAsUndefined, boolean useUnknown) {
		this.name = name;
		this.writer = writer;
		this.treatNullAsUndefined = treatNullAsUndefined;
		this.useUnknown = useUnknown;
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
		writer.line("public get " + name + "() : " + type + "$Imm " + variables + " {");
		writer.indentIn();
		writer.line("return new " + type + "$Imm" + variables + "(this._delegate.root, this._delegate.extend(\"" + name + "\"));");
		writer.indentOut();
		writer.line("}");
	}

	private void primitiveArrayAccessor(String type) {
		writer.line("public get " + name + "() : jsonInterfaceGenerator.PrimitiveArrayWrapper<" + type + "> {");
		writer.indentIn();
		writer.line("return new jsonInterfaceGenerator.PrimitiveArrayWrapper<" + type + ">(this._delegate.root, this._delegate.extend(\"" +
				name + "\"));");
		writer.indentOut();
		writer.line("}");
	}

	private void objectArrayAccessor(String intfType, String wrapperType) {
		writer.line("public get " + name + "(): jsonInterfaceGenerator.PrimitiveArrayWrapper<" + intfType + "> {");
		writer.indentIn();
		writer.line(
				"return new jsonInterfaceGenerator.PrimitiveArrayWrapper<" + intfType + ">(this._delegate.root, this._delegate.extend(\"" +
						name + "\"));");
		writer.indentOut();
		writer.line("}");
	}

	@Override
	public Object visit(JObject jObject) {
		if (jObject.isConstructible()) {
			objectAccessor(jObject.getReference(), "");
		}
		return null;
	}

	@Override
	public Object visit(JAny jAny) {
		primitiveAccessor(useUnknown ? "unknown" : "any");
		return null;

	}

	@Override
	public Object visit(JArray jArray) {
		String interfaceName = jArray.getElementType().accept(new TypeUsageProducer(null, treatNullAsUndefined, useUnknown));
		if (jArray.getElementType().needsWrapping()) {
			if (jArray.getElementType() instanceof JTypeVariable) {
				primitiveArrayAccessor(interfaceName);
			} else {
				TypeUsageProducer typeUsageProducer = new TypeUsageProducer("$Imm", treatNullAsUndefined, useUnknown);
				String wrapperName = jArray.getElementType().accept(typeUsageProducer);
				objectArrayAccessor(interfaceName, wrapperName);
			}
		} else {
			primitiveArrayAccessor(interfaceName);
		}
		return null;

	}

	@Override
	public Object visit(JBoolean jBoolean) {
		primitiveAccessor("boolean");
		return null;

	}

	@Override
	public Object visit(JEnum jEnum) {
		primitiveAccessor(jEnum.getReference());
		return null;

	}

	@Override
	public Object visit(JNumber jNumber) {
		primitiveAccessor("number");
		return null;

	}

	@Override
	public Object visit(JString jString) {
		primitiveAccessor("string");
		return null;
	}

	@Override
	public Object visit(JVoid jVoid) {
		return null;
	}

	@Override
	public Object visit(JSpecialization jSpecialization) {
		TypeUsageProducer tup = new TypeUsageProducer(null, TypeUsageProducer.WillBeSpecialized.YES, treatNullAsUndefined, useUnknown);
		String type = jSpecialization.getBase().accept(tup) + writeSpecializedTypes(jSpecialization, tup);
		primitiveAccessor(type);
		return null;
	}

	@Override
	public Object visit(JTypeVariable jTypeVariable) {
		primitiveAccessor(jTypeVariable.getName());
		return null;

	}

	@Override
	public Object visit(JMap jMap) {
		primitiveAccessor(
				"{[name: string]:" + jMap.getValueType().accept(new TypeUsageProducer(null, treatNullAsUndefined, useUnknown)) + "}");
		return null;

	}

	@Override
	public Object visit(JUnionType jUnionType) {
		JType stripped = jUnionType.getStripped();
		if (stripped.needsWrapping()) {
			if (stripped instanceof JArray) {
				return visit((JArray) stripped);
			} else if (stripped instanceof JObject) {
				if (stripped.hasTypeVariables()) {
					String typeString = stripped.accept(
							new TypeUsageProducer(null, TypeUsageProducer.WillBeSpecialized.YES, treatNullAsUndefined, useUnknown));
					String variables = writeTypeVariables(stripped, new TypeUsageProducer(null, treatNullAsUndefined, useUnknown));
					objectAccessor(typeString, variables);
					return null;
				} else {
					String typeString = stripped.accept(new TypeUsageProducer(null, treatNullAsUndefined, useUnknown));
					objectAccessor(typeString, "");
					return null;
				}
			} else if (stripped instanceof JSpecialization) {
				return visit((JSpecialization) stripped);
			}
		}
		String typeString = jUnionType.accept(new TypeUsageProducer(null, treatNullAsUndefined, useUnknown));
		primitiveAccessor(typeString);
		return null;
	}

	@Override
	public Object visit(JNull jNull) {
		primitiveAccessor(jNull.accept(new TypeUsageProducer(null, treatNullAsUndefined, useUnknown)));
		return null;
	}

	@Override
	public Object visit(JWildcard jWildcard) {
		primitiveAccessor(jWildcard.accept(new TypeUsageProducer(null, treatNullAsUndefined, useUnknown)));
		return null;
	}
}

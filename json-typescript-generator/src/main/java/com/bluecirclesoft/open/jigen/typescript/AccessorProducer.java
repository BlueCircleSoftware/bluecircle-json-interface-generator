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

	public AccessorProducer(String name, OutputHandler writer) {
		this.name = name;
		this.writer = writer;
	}

	private void primitiveAccessor(String type) {
		writer.line("public get " + name + "() : " + type + " {");
		writer.indentIn();
		writer.line("return this.$base.get(\"" + name + "\");");
		writer.indentOut();
		writer.line("}");
		writer.line("public set " + name + "(v : " + type + ") {");
		writer.indentIn();
		writer.line("this.$base.set(\"" + name + "\", v);");
		writer.indentOut();
		writer.line("}");
	}

	private void objectAccessor(String type) {
		writer.line("public get " + name + "() : " + type + "$Imm {");
		writer.indentIn();
		writer.line(
				"return new " + type + "$Imm(new jsonInterfaceGenerator.ObjectWrapper<" + type + ">(this.$base, \"" + name + "\", " + type +
						"$Imm.make));");
		writer.indentOut();
		writer.line("}");
	}

	private void primitiveArrayAccessor(String type) {
		writer.line("public get " + name + "() : jsonInterfaceGenerator.ArrayWrapper<" + type + "> {");
		writer.indentIn();
		writer.line("return new jsonInterfaceGenerator.ArrayWrapper<" + type + ">(this.$base, \"" + name + "\", () => []);");
		writer.indentOut();
		writer.line("}");
	}

	private void objectArrayAccessor(String intfType, String wrapperType) {
		writer.line(
				"public get " + name + "(): jsonInterfaceGenerator.WrappedElementArrayWrapper<" + intfType + ", " + wrapperType + "> {");
		writer.indentIn();
		writer.line("let newLeaf = new jsonInterfaceGenerator.ArrayWrapper<" + intfType + ">(this.$base, \"" + name + "\", () => {");
		writer.indentIn();
		writer.line("return [];");
		writer.indentOut();
		writer.line("});");
		writer.line("return new jsonInterfaceGenerator.WrappedElementArrayWrapper<" + intfType + ", " + wrapperType +
				">(newLeaf, (parent: jsonInterfaceGenerator.ArrayWrapper<" + intfType + ">, index: number) => {");
		writer.indentIn();
		writer.line("let wrapperLeaf = new jsonInterfaceGenerator.ObjectWrapper<" + intfType + ">(parent, index, () => {");
		writer.indentIn();
		writer.line("return " + wrapperType + ".make();");
		writer.indentOut();
		writer.line("});");
		writer.line("return new " + wrapperType + "(wrapperLeaf);");
		writer.indentOut();
		writer.line("});");
		writer.indentOut();
		writer.line("}");
	}

	@Override
	public Object visit(JObject jObject) {
		if (jObject.isConstructible()) {
			objectAccessor(jObject.getReference());
		}
		return null;
	}

	@Override
	public Object visit(JAny jAny) {
		primitiveAccessor("any");
		return null;

	}

	@Override
	public Object visit(JArray jArray) {
		String interfaceName = jArray.getElementType().accept(new TypeUsageProducer(null));
		if (jArray.getElementType().needsWrapping()) {
			if (jArray.getElementType() instanceof JTypeVariable) {
				// TODO skipping this for now - the immutable wrapper wants to auto-create objects all the way down, but we have a type
				// variable here, so there's no good way to implement that.  Holding off for now, until I understand a good use case
				log.info("Cannot create immutable wrapper for array of type {}", new TypeUsageProducer(null).visit(jArray));
			} else if (!jArray.getElementType().isConstructible()) {
			} else {
				TypeUsageProducer typeUsageProducer = new TypeUsageProducer("$Imm");
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
		TypeUsageProducer tup = new TypeUsageProducer(null, TypeUsageProducer.WillBeSpecialized.YES);
		StringBuilder sb = new StringBuilder();
		sb.append(jSpecialization.getBase().accept(tup));
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
		String type = sb.toString();
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
		primitiveAccessor("{[name: string]:" + jMap.getValueType().accept(new TypeUsageProducer(null)) + "}");
		return null;

	}

	@Override
	public Object visit(JUnionType jUnionType) {
		if (jUnionType.needsWrapping()) {
			JUnionType stripped = jUnionType.getStripped();
			String typeString = stripped.accept(new TypeUsageProducer(null));
			if (stripped.getMembers().size() == 1) {
				return stripped.getMembers().get(0).accept(this);
			} else {
				if (stripped.needsWrapping()) {
					primitiveAccessor(typeString);
				} else {
					objectAccessor(typeString);
				}
			}
		} else {
			String typeString = jUnionType.accept(new TypeUsageProducer(null));
			primitiveAccessor(typeString);
		}
		return null;
	}

	@Override
	public Object visit(JNull jNull) {
		primitiveAccessor(jNull.accept(new TypeUsageProducer(null)));
		return null;
	}

	@Override
	public Object visit(JWildcard jWildcard) {
		primitiveAccessor(jWildcard.accept(new TypeUsageProducer(null)));
		return null;
	}
}

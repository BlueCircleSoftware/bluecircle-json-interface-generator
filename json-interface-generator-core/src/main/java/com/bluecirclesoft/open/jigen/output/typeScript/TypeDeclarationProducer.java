/*
 * Copyright 2015 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.output.typeScript;

import java.util.Map;

import com.bluecirclesoft.open.jigen.model.JAny;
import com.bluecirclesoft.open.jigen.model.JArray;
import com.bluecirclesoft.open.jigen.model.JBoolean;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JMap;
import com.bluecirclesoft.open.jigen.model.JNumber;
import com.bluecirclesoft.open.jigen.model.JObject;
import com.bluecirclesoft.open.jigen.model.JSpecialization;
import com.bluecirclesoft.open.jigen.model.JString;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.bluecirclesoft.open.jigen.model.JTypeVisitor;
import com.bluecirclesoft.open.jigen.model.JVoid;

/**
 * TODO document me
 */
class TypeDeclarationProducer implements JTypeVisitor<Integer> {

	private final OutputHandler writer;

	private final TypeScriptProducer producer;

	public TypeDeclarationProducer(TypeScriptProducer typeScriptProducer, OutputHandler writer) {
		this.writer = writer;
		this.producer = typeScriptProducer;
	}

	@Override
	public Integer visit(JObject jObject) {
		makeInterfaceDeclaration(jObject);
		return null;
	}

	@Override
	public Integer visit(JAny jAny) {
		return null;
	}

	@Override
	public Integer visit(JArray jArray) {
		return null;
	}

	@Override
	public Integer visit(JBoolean jBoolean) {
		return null;
	}

	@Override
	public Integer visit(JEnum jEnum) {
		writer.line();
		writer.line("export enum " + jEnum.getName() + " {");
		writer.indentIn();
		int count = 0;
		for (String value : jEnum.getValues()) {
			String suffix;
			if (count == jEnum.getValues().size() - 1) {
				suffix = "";
			} else {
				suffix = ",";
			}
			writer.line(value + suffix);
			count++;
		}
		writer.indentOut();
		writer.line("}");
		return null;
	}

	@Override
	public Integer visit(JNumber jNumber) {
		return null;
	}

	@Override
	public Integer visit(JString jString) {
		return null;
	}

	@Override
	public Integer visit(JVoid jVoid) {
		return null;
	}

	@Override
	public Integer visit(JSpecialization jSpecialization) {
		return null;
	}

	@Override
	public Integer visit(JTypeVariable jTypeVariable) {
		return null;
	}

	@Override
	public Integer visit(JMap jMap) {
		return null;
	}

	private void makeInterfaceDeclaration(JObject intf) {
		CreateConstructorVisitor createConstructorVisitor = new CreateConstructorVisitor();
		String interfaceLabel = intf.getName();
		String interfaceType = intf.accept(new TypeVariableProducer());
		writer.line();
		String declLine = "export interface " + interfaceType + " {";
		writer.line(declLine);
		writer.indentIn();
		TypeUsageProducer typeUsageProducer = new TypeUsageProducer();
		for (Map.Entry<String, JObject.Field> prop : intf.getFields().entrySet()) {
			String typeString = prop.getValue().getType().accept(typeUsageProducer);
			writer.line(prop.getKey() + (prop.getValue().isRequired() ? "" : "?") + ": " + typeString + ";");
		}
		writer.indentOut();
		writer.line("}");

		if (producer.isProduceAccessorFunctionals()) {
			for (Map.Entry<String, JObject.Field> prop : intf.getFields().entrySet()) {
				String propertyName = prop.getKey();
				JType propertyType = prop.getValue().getType();
				String typeString = propertyType.accept(typeUsageProducer) + (prop.getValue().isRequired() ? "" : " | undefined");
				String ctor = propertyType.accept(createConstructorVisitor);
				if (intf.getTypeVariables().size() == 0 && ctor != null) {
					// TODO handle interfaces with type variables properly. The straightforward thing to do would be to give the function
					// a type parameter, and pass in the appropriate constructor as a parameter. But I'm not really sure how useful this
					// would be.

					// Note if the ctor is null here, that means I don't actually know how to produce a construtcor. For now, we'll just
					// skip this function.
					String retType = "jsonInterfaceGenerator" + ".MemberAccessorImpl<" + interfaceLabel + "," + typeString + ">";
					writer.line("export function " + interfaceLabel + "$" + propertyName + "() : " + retType + " { ");
					writer.indentIn();
					writer.line("return new " + retType + "('" + propertyName + "', " + ctor + ");");
					writer.indentOut();
					writer.line("}");
				}
			}
		}
	}

}

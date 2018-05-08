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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

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
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.bluecirclesoft.open.jigen.model.JTypeVisitor;
import com.bluecirclesoft.open.jigen.model.JUnionType;
import com.bluecirclesoft.open.jigen.model.JVoid;
import com.bluecirclesoft.open.jigen.model.JWildcard;

/**
 * TODO document me
 */
class TypeDeclarationProducer implements JTypeVisitor<Integer> {

	private final OutputHandler writer;

	private final Writer producer;

	private final boolean produceImmutable;

	public TypeDeclarationProducer(Writer typeScriptProducer, OutputHandler writer, boolean produceImmutable) {
		this.writer = writer;
		this.producer = typeScriptProducer;
		this.produceImmutable = produceImmutable;
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
		String name = jEnum.getName();
		// emit enum
		writer.line("export enum " + name + " {");
		writer.indentIn();
		int count = 0;
		List<JEnum.EnumDeclaration> values = jEnum.getValues();
		int valueCount = values.size() - 1;
		for (JEnum.EnumDeclaration value : values) {
			String suffix;
			if (count == valueCount) {
				suffix = "";
			} else {
				suffix = ",";
			}
			if (jEnum.getEnumType() == JEnum.EnumType.STRING) {
				writer.line(value.getName() + " = \"" + StringEscapeUtils.escapeEcmaScript(value.getSerializedValue()) + "\"" + suffix);
			} else {
				writer.line(value.getName() + suffix);
			}
			count++;
		}
		writer.indentOut();
		writer.line("}");
		// enum already has index -> name and name -> index, but we will emit index -> enum constant and name -> enum constant
		writer.line("export const " + name + "_values : jsonInterfaceGenerator.EnumReverseLookup<" + name + "> = {};");
		for (JEnum.EnumDeclaration value : values) {
			writer.line(name + "_values[" + value.getNumericValue() + "] = " + name + "." + value.getName() + ";");
			writer.line(name + "_values[\"" + StringEscapeUtils.escapeEcmaScript(value.getSerializedValue()) + "\"] = " + name + "." +
					value.getName() + ";");
		}
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

	@Override
	public Integer visit(JUnionType jUnionType) {
		return null;
	}

	@Override
	public Integer visit(JNull jNull) {
		return null;
	}

	@Override
	public Integer visit(JWildcard jWildcard) {
		return null;
	}


	private void makeInterfaceDeclaration(JObject intf) {
		String interfaceLabel = intf.getName();
		String definterfaceType = intf.accept(new TypeVariableProducer(UsageLocation.DEFINITION, null));
		writer.line();
		String declLine = "export interface " + definterfaceType + " {";
		writer.line(declLine);
		writer.indentIn();
		TypeUsageProducer typeUsageProducer = new TypeUsageProducer(null);
		for (Map.Entry<String, JObject.Field> prop : intf.getFields().entrySet()) {
			String typeString = prop.getValue().getType().accept(typeUsageProducer);
			writer.line(prop.getKey() + ": " + typeString + ";");
		}
		writer.indentOut();
		writer.line("}");

		if (produceImmutable) {
			String immutableInterfaceType = intf.accept(new TypeVariableProducer(UsageLocation.DEFINITION, "$Imm"));
			declLine = "export class " + immutableInterfaceType + " {";
			writer.line(declLine);
			writer.indentIn();
			String typeVars = "";
			if (!intf.getTypeVariables().isEmpty()) {
				StringBuilder typeVarsBuilder = new StringBuilder();
				typeVarsBuilder.append('<');
				boolean needsComma = false;
				for (JTypeVariable var : intf.getTypeVariables()) {
					if (needsComma) {
						typeVarsBuilder.append(',');
					} else {
						needsComma = true;
					}
					typeVarsBuilder.append(var.getName());
				}
				typeVarsBuilder.append('>');
				typeVars = typeVarsBuilder.toString();
			}
			if (intf.getNewObjectJson() != null) {
				writer.line("static make" + typeVars + "() : " + interfaceLabel + typeVars + " { ");
				writer.indentIn();
				writer.line("return " + intf.getNewObjectJson() + ";");
				writer.indentOut();
				writer.line("}");
			}

			String usinterfaceType = intf.accept(new TypeVariableProducer(UsageLocation.USAGE, null));
			writer.line("private $base : jsonInterfaceGenerator.DirectWrapper<" + usinterfaceType + ">;");
			writer.line("public constructor(base: jsonInterfaceGenerator.DirectWrapper<" + usinterfaceType + ">) {");
			writer.indentIn();
			writer.line("this.$base = base;");
			writer.indentOut();
			writer.line("}");
			for (Map.Entry<String, JObject.Field> prop : intf.getFields().entrySet()) {
				AccessorProducer accessorProducer = new AccessorProducer(prop.getKey(), writer);
				prop.getValue().getType().accept(accessorProducer);
			}
			writer.indentOut();
			writer.line("}");
		}
	}

}

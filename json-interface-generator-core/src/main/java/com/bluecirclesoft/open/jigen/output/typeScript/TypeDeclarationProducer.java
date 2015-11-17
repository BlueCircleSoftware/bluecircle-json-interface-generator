package com.bluecirclesoft.open.jigen.output.typeScript;

import com.bluecirclesoft.open.jigen.model.JAny;
import com.bluecirclesoft.open.jigen.model.JArray;
import com.bluecirclesoft.open.jigen.model.JBoolean;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JMap;
import com.bluecirclesoft.open.jigen.model.JNumber;
import com.bluecirclesoft.open.jigen.model.JObject;
import com.bluecirclesoft.open.jigen.model.JSpecialization;
import com.bluecirclesoft.open.jigen.model.JString;
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.bluecirclesoft.open.jigen.model.JTypeVisitor;
import com.bluecirclesoft.open.jigen.model.JVoid;

import java.util.Map;

/**
 * TODO document me
 */
class TypeDeclarationProducer implements JTypeVisitor<Integer> {

	private final OutputHandler writer;

	public TypeDeclarationProducer(OutputHandler writer) {
		this.writer = writer;
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
		String declLine = "export interface " +
				intf.accept(new TypeVariableProducer()) +
				" {";
		writer.line();
		writer.line(declLine);
		writer.indentIn();
		for (Map.Entry<String, JObject.Field> prop : intf.getFields().entrySet()) {
			String typeString = prop.getValue().getType().accept(new TypeUsageProducer());
			writer.line(
					prop.getKey() + (prop.getValue().isRequired() ? "" : "?") + ": " + typeString +
							";");
		}
		writer.indentOut();
		writer.line("}");
	}


}

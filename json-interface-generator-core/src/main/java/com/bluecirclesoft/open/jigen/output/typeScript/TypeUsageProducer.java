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
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.bluecirclesoft.open.jigen.model.JTypeVisitor;
import com.bluecirclesoft.open.jigen.model.JVoid;

/**
 * TODO document me
 */
class TypeUsageProducer implements JTypeVisitor<String> {

	@Override
	public String visit(JObject jObject) {
		return jObject.getName();
	}

	@Override
	public String visit(JAny jAny) {
		return "any";
	}

	@Override
	public String visit(JArray jArray) {
		return jArray.getElementType().accept(this) + "[]";
	}

	@Override
	public String visit(JBoolean jBoolean) {
		return "boolean";
	}

	@Override
	public String visit(JEnum jEnum) {
		return jEnum.getName();
	}

	@Override
	public String visit(JNumber jNumber) {
		return "number";
	}

	@Override
	public String visit(JString jString) {
		return "string";
	}

	@Override
	public String visit(JVoid jVoid) {
		return "void";
	}

	@Override
	public String visit(JSpecialization jSpecialization) {
		StringBuilder sb = new StringBuilder();
		sb.append(jSpecialization.getBase().accept(this));
		sb.append("<");
		boolean needsComma = false;
		for (JType param : jSpecialization.getParameters()) {
			if (needsComma) {
				sb.append(", ");
			} else {
				needsComma = true;
			}
			sb.append(param.accept(this));
		}
		sb.append(">");
		return sb.toString();
	}

	@Override
	public String visit(JTypeVariable jTypeVariable) {
		return jTypeVariable.getName();
	}

	@Override
	public String visit(JMap jMap) {
		return "{[name: string]:" + jMap.getValueType().accept(this) + "}";
	}
}

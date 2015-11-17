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
public class TypeVariableProducer implements JTypeVisitor<String> {

	@Override
	public String visit(JObject jObject) {
		StringBuilder sb = new StringBuilder();
		sb.append(jObject.getName());
		if (!jObject.getTypeVariables().isEmpty()) {
			sb.append("<");
			boolean needsComma = false;
			for (JTypeVariable var : jObject.getTypeVariables()) {
				if (needsComma) {
					sb.append(", ");
				} else {
					needsComma = true;
				}
				sb.append(var.accept(this));
			}
			sb.append(">");
		}
		return sb.toString();
	}

	@Override
	public String visit(JAny jAny) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String visit(JArray jArray) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String visit(JBoolean jBoolean) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String visit(JEnum jEnum) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String visit(JNumber jNumber) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String visit(JString jString) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String visit(JVoid jVoid) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String visit(JSpecialization jSpecialization) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String visit(JTypeVariable var) {
		StringBuilder sb = new StringBuilder();
		sb.append(var.getName());
		if (!var.getIntersectionBounds().isEmpty()) {
			sb.append(" extends");
			boolean needsAmpersand = false;
			for (JType bound : var.getIntersectionBounds()) {
				if (needsAmpersand) {
					sb.append("&");
				} else {
					needsAmpersand = true;
				}
				sb.append(bound.accept(this));
			}
		}
		return sb.toString();
	}

	@Override
	public String visit(JMap jMap) {
		throw new RuntimeException("not implemented");
	}
}

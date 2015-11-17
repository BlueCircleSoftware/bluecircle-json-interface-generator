package com.bluecirclesoft.open.jigen.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO document me
 */
public class JObject extends JToplevelType {

	public class Field {

		private final String name;

		private final JType type;

		private final boolean required;

		private Field(String name, JType type, boolean required) {
			this.name = name;
			this.type = type;
			this.required = required;
		}

		public String getName() {
			return name;
		}

		public JType getType() {
			return type;
		}

		public boolean isRequired() {
			return required;
		}
	}

	private String name;

	private List<JTypeVariable> typeVariables = new ArrayList<>();

	private final LinkedHashMap<String, Field> fields = new LinkedHashMap<>();

	public JObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JTypeVariable> getTypeVariables() {
		return typeVariables;
	}

	public void setTypeVariables(List<JTypeVariable> typeVariables) {
		this.typeVariables = typeVariables;
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	public void declareProperty(String name) {
		fields.put(name, null);
	}

	public void makeProperty(String name, JType type, boolean required) {
		if (fields.get(name) != null) {
			throw new RuntimeException(name + " already defined on Object " + this.name);
		}
		if (type == null) {
			throw new RuntimeException("type cannot be null");
		}
		fields.put(name, new Field(name, type, required));
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

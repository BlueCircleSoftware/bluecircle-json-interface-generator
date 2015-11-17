package com.bluecirclesoft.open.jigen.model;

public class JsonProperty {

	private final String name;

	private JsonType type;

	public JsonProperty(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public JsonType getType() {
		return type;
	}

	public void setType(JsonType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "JsonProperty{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}

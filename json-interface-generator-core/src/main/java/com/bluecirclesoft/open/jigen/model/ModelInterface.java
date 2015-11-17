package com.bluecirclesoft.open.jigen.model;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO document me
 */
public class ModelInterface {

	private final String name;

	private Map<String, JsonProperty> properties = new HashMap<>();

	public ModelInterface(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public JsonProperty makeProperty(String key) {
		JsonProperty property = properties.get(key);
		if (property != null) {
			return property;
		}
		property = new JsonProperty(key);
		properties.put(key, property);
		return property;
	}

	@Override
	public String toString() {
		return "ModelInterface{" +
				"name='" + name + '\'' +
				", properties=" + properties +
				'}';
	}

	public Map<String, JsonProperty> getProperties() {
		return properties;
	}
}

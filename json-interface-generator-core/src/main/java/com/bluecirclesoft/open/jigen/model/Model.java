package com.bluecirclesoft.open.jigen.model;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO document me
 */
public class Model {

	private final Map<Type, JType> interfaces = new HashMap<>();

	private final Map<String, Endpoint> endpoints = new HashMap<>();

	public boolean hasId(String id) {
		return interfaces.containsKey(id);
	}

	public Collection<JType> getInterfaces() {
		return interfaces.values();
	}

	public JType getInterface(JType id) {
		return interfaces.get(id);
	}

	public void addType(Type type, JType jType) {
		if (jType == null) {
			throw new RuntimeException("Failed to translate type " + type);
		}
		interfaces.put(type, jType);
	}

	public Endpoint createEndpoint(String name) {
		Endpoint endpoint = new Endpoint(name);
		endpoints.put(name, endpoint);
		return endpoint;
	}

	@Override
	public String toString() {
		return "Model{" +
				"interfaces=" + interfaces +
				'}';
	}
}

package com.bluecirclesoft.open.jigen.inputJackson;

import java.lang.reflect.Type;

import com.bluecirclesoft.open.jigen.model.JObject;

/**
 * When we encounter a property that has a type we haven't converted to a JType yet, we create a DeferredProperty instance to go back and
 * add the property later, after all the classes have been processed.
 */
public class DeferredProperty {

	private final JObject jObject;

	private final String name;

	private final Type type;

	private final boolean required;

	public DeferredProperty(JObject jObject, String name, Type type, boolean required) {
		this.jObject = jObject;
		this.name = name;
		this.type = type;
		this.required = required;
	}

	public JObject getjObject() {
		return jObject;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public boolean isRequired() {
		return required;
	}
}

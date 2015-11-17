package com.bluecirclesoft.open.jigen.model;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO document me
 */
public class JEnum extends JToplevelType {

	private String name;

	private final Set<String> values = new HashSet<>();

	public JEnum() {
	}

	public JEnum(String name, Set<String> values) {
		this.name = name;
		this.values.addAll(values);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getValues() {
		return values;
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}

package com.bluecirclesoft.open.jigen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO document me
 */
public class JTypeVariable extends JType {

	private String name;

	private List<JType> intersectionBounds = new ArrayList<>();

	public JTypeVariable() {
	}

	public JTypeVariable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JType> getIntersectionBounds() {
		return intersectionBounds;
	}

	public void setIntersectionBounds(List<JType> intersectionBounds) {
		this.intersectionBounds = intersectionBounds;
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

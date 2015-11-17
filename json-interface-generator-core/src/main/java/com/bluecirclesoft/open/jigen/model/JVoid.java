package com.bluecirclesoft.open.jigen.model;

/**
 * TODO document me
 */
public class JVoid extends JType {

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

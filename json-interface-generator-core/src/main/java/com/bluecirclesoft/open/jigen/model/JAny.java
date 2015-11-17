package com.bluecirclesoft.open.jigen.model;

/**
 * TODO document me
 */
public class JAny extends JType {

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

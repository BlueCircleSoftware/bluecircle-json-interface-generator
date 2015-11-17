package com.bluecirclesoft.open.jigen.model;

/**
 * TODO document me
 */
public class JMap extends JType {

	private JType valueType;

	public JMap() {
	}

	public JMap(JType valueType) {
		this.valueType = valueType;
	}

	public JType getValueType() {
		return valueType;
	}

	public void setValueType(JType valueType) {
		this.valueType = valueType;
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

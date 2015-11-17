package com.bluecirclesoft.open.jigen.model;

/**
 * TODO document me
 */
public class JSpecialization extends JType {

	private JType base;

	private JType[] parameters;

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JType getBase() {
		return base;
	}

	public void setBase(JType base) {
		this.base = base;
	}

	public JType[] getParameters() {
		return parameters;
	}

	public void setParameters(JType[] parameters) {
		this.parameters = parameters;
	}
}

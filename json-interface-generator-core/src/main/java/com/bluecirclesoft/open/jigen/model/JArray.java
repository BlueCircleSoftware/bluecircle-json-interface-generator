package com.bluecirclesoft.open.jigen.model;

/**
 * TODO document me
 */
public class JArray extends JType {

	private JType indexType;

	private JType elementType;

	public JType getIndexType() {
		return indexType;
	}

	public void setIndexType(JType indexType) {
		this.indexType = indexType;
	}

	public JType getElementType() {
		return elementType;
	}

	public void setElementType(JType elementType) {
		this.elementType = elementType;
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

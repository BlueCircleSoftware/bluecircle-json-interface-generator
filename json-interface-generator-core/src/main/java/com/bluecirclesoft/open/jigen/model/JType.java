package com.bluecirclesoft.open.jigen.model;

/**
 * TODO document me
 */
abstract public class JType {

	abstract public <T> T accept(JTypeVisitor<T> visitor);

}

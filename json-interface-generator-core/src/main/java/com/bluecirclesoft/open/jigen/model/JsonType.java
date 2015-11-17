package com.bluecirclesoft.open.jigen.model;

/**
 * TODO document me
 */
public class JsonType {

	private BaseType type;

	private String classId;

	/**
	 * TODO array needs to be its own type to handle multi-dimanesional arrays
	 */
	private boolean array;

	private boolean optional;

	private JsonType(BaseType type, String classId, boolean array) {
		this.type = type;
		this.classId = classId;
		this.array = array;
	}

	public BaseType getType() {
		return type;
	}

	public void setType(BaseType type) {
		this.type = type;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public boolean isArray() {
		return array;
	}

	public void setArray(boolean array) {
		this.array = array;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	@Override
	public String toString() {
		return "JsonType{" +
				"type=" + type +
				", classId='" + classId + '\'' +
				", array=" + array +
				", optional=" + optional +
				'}';
	}

	public static JsonType makeClass(String id) {
		return new JsonType(BaseType.CLASS, id, false);
	}

	public static JsonType makeSimple(BaseType baseType) {
		return new JsonType(baseType, null, false);
	}

	public static JsonType makeArrayOf(JsonType base) {
		return new JsonType(base.getType(), base.classId, true);
	}
}

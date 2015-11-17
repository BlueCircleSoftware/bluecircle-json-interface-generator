/*
 * Copyright 2015 Blue Circle Software, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

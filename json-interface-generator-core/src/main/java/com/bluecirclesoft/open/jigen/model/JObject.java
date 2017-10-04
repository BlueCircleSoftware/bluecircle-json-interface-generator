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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * TODO document me
 */
public class JObject extends JToplevelType {

	public class Field implements Serializable {

		private final String name;

		private final JType type;

		private Field(String name, JType type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public JType getType() {
			return type;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this).append("name", name).append("type", type).toString();
		}
	}

	private String name;

	private List<JTypeVariable> typeVariables = new ArrayList<>();

	private final LinkedHashMap<String, Field> fields = new LinkedHashMap<>();

	private String newObjectJson;

	public JObject(String name) {
		this.name = name;
		JType.createdTypes.add(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JTypeVariable> getTypeVariables() {
		return typeVariables;
	}

	public void setTypeVariables(List<JTypeVariable> typeVariables) {
		this.typeVariables = typeVariables;
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	/**
	 * Get the JSON for a newly created instance.
	 *
	 * @return the JSON, or null if it could not be produced.
	 */
	public String getNewObjectJson() {
		return newObjectJson;
	}

	public void setNewObjectJson(String newObjectJson) {
		this.newObjectJson = newObjectJson;
	}

	public void declareProperty(String name) {
		fields.put(name, null);
	}

	public void makeProperty(String name, JType type, boolean required) {
		if (fields.get(name) != null) {
			throw new RuntimeException(name + " already defined on Object " + this.name);
		}
		if (type == null) {
			throw new RuntimeException("type cannot be null");
		}
		JType realType;
		if (!required) {
			// if field is not required, add a union with null
			JUnionType union = new JUnionType();
			union.getMembers().add(type);
			union.getMembers().add(new JNull());
			realType = union;
		} else {
			realType = type;
		}
		fields.put(name, new Field(name, realType));
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("typeVariables", typeVariables).append("fields", fields).toString();
	}

	@Override
	public boolean needsWrapping() {
		return true;
	}
}

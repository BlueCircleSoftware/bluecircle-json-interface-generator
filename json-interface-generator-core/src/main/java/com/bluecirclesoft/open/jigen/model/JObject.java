/*
 * Copyright 2019 Blue Circle Software, LLC
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
 *
 */

package com.bluecirclesoft.open.jigen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a TypeScript 'interface', with possible generic type variables
 */
@Getter
public class JObject extends JToplevelType {

	@Getter
	public static final class Field implements Serializable {

		private final String name;

		private final JType type;

		private Field(String name, JType type) {
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this).append("name", name).append("type", type).toString();
		}
	}

	@Setter
	private String name;

	@Setter
	private List<JTypeVariable> typeVariables = new ArrayList<>();

	// sort for stability of output
	private final SortedMap<String, Field> fields = new TreeMap<>();

	/**
	 * -- GETTER --
	 * Get the JSON for a newly created instance.
	 */
	@Setter
	private String newObjectJson;

	@Setter
	private String typeDiscriminatorField;

	@Setter
	private String typeDiscriminatorValue;

	@Setter
	private Class<?> sourceClass;

	private final SortedMap<String, JObject> superclasses = new TreeMap<>();

	private final SortedMap<String, JObject> subclasses = new TreeMap<>();

	public JObject(String name, Class<?> sourceClass) {
		this.name = name;
		this.sourceClass = sourceClass;
		JType.createdTypes.add(this);
	}

	public Set<Map.Entry<String, Field>> getFieldEntries() {
		return fields.entrySet();
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
	public void accept(JTypeVisitorVoid visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("typeVariables", typeVariables).append("fields", fields).toString();
	}

	@Override
	public boolean needsWrapping() {
		return true;
	}

	@Override
	public boolean isConstructible() {
		return newObjectJson != null;
	}

	@Override
	public boolean hasTypeVariables() {
		return !typeVariables.isEmpty();
	}

}

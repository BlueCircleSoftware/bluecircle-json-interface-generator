/*
 * Copyright 2018 Blue Circle Software, LLC
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Models a TypeScript enum.
 * <p>There is a semi-weird complication with enums, in that in Jackson, you can use {@code @JsonProperty} to change the serialization
 * value of the enum. What should we do with this information? Here's the executive decision: when building the TypeScript enum, give the
 * enunm constants the same name as their Java counterparts.  When building the reverse-lookup table, use the serialized values.
 * </p>
 */
public class JEnum extends JToplevelType {

	/**
	 * Should this be a string-based enum, or a number-based enum?
	 */
	public enum EnumType {
		STRING,
		NUMERIC
	}

	public static class EnumDeclaration {

		private final String name;

		private final int numericValue;

		private final String serializedValue;

		public EnumDeclaration(String name, int numericValue, String serializedValue) {
			this.name = name;
			this.numericValue = numericValue;
			this.serializedValue = serializedValue;
		}

		public String getName() {
			return name;
		}

		public int getNumericValue() {
			return numericValue;
		}

		public String getSerializedValue() {
			return serializedValue;
		}
	}

	private String name;

	private final EnumType enumType;

	private final List<EnumDeclaration> values = new ArrayList<>();

	public JEnum(String name, EnumType enumType, List<EnumDeclaration> values) {
		this.name = name;
		this.enumType = enumType;
		this.values.addAll(values);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EnumDeclaration> getValues() {
		return values;
	}

	public EnumType getEnumType() {
		return enumType;
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("values", values).toString();
	}

	@Override
	public boolean needsWrapping() {
		return false;
	}

	@Override
	public boolean isSpecializable() {
		return false;
	}
}

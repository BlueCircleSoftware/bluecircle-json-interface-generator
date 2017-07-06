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

import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * TODO document me
 */
public class JEnum extends JToplevelType {

	private String name;

	private final LinkedHashSet<String> values = new LinkedHashSet<>();

	public JEnum() {
	}

	public JEnum(String name, List<String> values) {
		this.name = name;
		this.values.addAll(values);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedHashSet<String> getValues() {
		return values;
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("values", values).toString();
	}
}

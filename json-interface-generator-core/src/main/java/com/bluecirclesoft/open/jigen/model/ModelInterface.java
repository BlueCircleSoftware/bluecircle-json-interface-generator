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

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO document me
 */
public class ModelInterface {

	private final String name;

	private Map<String, JsonProperty> properties = new HashMap<>();

	public ModelInterface(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public JsonProperty makeProperty(String key) {
		JsonProperty property = properties.get(key);
		if (property != null) {
			return property;
		}
		property = new JsonProperty(key);
		properties.put(key, property);
		return property;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name)
				.append("properties", properties)
				.toString();
	}

	public Map<String, JsonProperty> getProperties() {
		return properties;
	}
}

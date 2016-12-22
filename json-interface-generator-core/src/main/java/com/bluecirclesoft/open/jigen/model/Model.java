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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO document me
 */
public class Model {

	private static final Logger log = LoggerFactory.getLogger(Model.class);

	private final Map<Type, JType> interfaces = new HashMap<>();

	private final Map<String, Endpoint> endpoints = new TreeMap<>();

	public Model() {
		log.info("New model: " + this);
	}

	public Collection<JType> getInterfaces() {
		return interfaces.values();
	}

	public void addType(Type type, JType jType) {
		if (jType == null) {
			throw new RuntimeException("Failed to translate type " + type);
		}
		interfaces.put(type, jType);
	}


	public Endpoint createEndpoint(String name) {
		Endpoint endpoint = new Endpoint(name);
		endpoints.put(name, endpoint);
		return endpoint;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("interfaces", interfaces).toString();
	}

	public boolean hasType(Type type) {
		return interfaces.containsKey(type);
	}

	public JType getType(Type key) {
		return interfaces.get(key);
	}

	public Iterable<Endpoint> getEndpoints() {
		return endpoints.values();
	}
}

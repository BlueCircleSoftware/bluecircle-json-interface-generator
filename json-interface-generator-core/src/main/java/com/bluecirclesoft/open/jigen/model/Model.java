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
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A model of what the server provides and accepts.
 */
public class Model implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(Model.class);

	private final Map<Type, JType> interfaces = new HashMap<>();

	private final Map<String, Endpoint> endpoints = new TreeMap<>();

	public Model() {
		logger.info("New model: " + this);
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
		String realName;
		if (endpoints.get(name) != null) {
			throw new RuntimeException("Multiple endpoints with same method name (cannot currently handle): " + name);
		} else {
			realName = name;
		}
		Endpoint endpoint = new Endpoint(realName);
		endpoints.put(realName, endpoint);
		return endpoint;
	}

	public void removeEndpoint(Endpoint endpoint) {
		endpoints.remove(endpoint.getId());
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

	public Endpoint getEndpoint(String name) {
		Endpoint endpoint = endpoints.get(name);
		if (endpoint == null) {
			throw new RuntimeException("No endpoint found named " + name);
		}
		return endpoint;
	}

	public void doGlobalCleanups() {
		// go through the classes, and put references to super- and subclasses in them all
		for (Map.Entry<Type, JType> entry : interfaces.entrySet()) {
			// if it's an object
			if (entry.getValue() instanceof JObject) {
				// do a breadth-first search of all the ancestors
				ArrayDeque<Class<?>> parentQueue = new ArrayDeque<>();
				parentQueue.add((Class<?>) entry.getKey());
				while (!parentQueue.isEmpty()) {
					Class<?> subClass = parentQueue.poll();
					// find superclass and all the interfaces we implement
					Set<Class<?>> supers = new HashSet<>();
					if (isInteresting(subClass.getSuperclass())) {
						supers.add(subClass.getSuperclass());
					}
					for (Class<?> intf : subClass.getInterfaces()) {
						if (isInteresting(intf)) {
							supers.add(intf);
						}
					}
					// map this subclass with each of its supers
					for (Class<?> superClass : supers) {
						if (interfaces.containsKey(superClass)) {
							JObject jSuper = (JObject) interfaces.get(superClass);
							JObject jSub = (JObject) interfaces.get(subClass);
							jSuper.getSubclasses().put(subClass.getName(), jSub);
							jSub.getSuperclasses().put(superClass.getName(), jSuper);
						}
					}
					parentQueue.addAll(supers);
				}
			}
		}
	}

	private boolean isInteresting(Class<?> superclass) {
		return superclass != null && superclass != Object.class;
	}

	public int getEndpointCount() {
		return endpoints.size();
	}

	public int getInterfaceCount() {
		return interfaces.size();
	}
}

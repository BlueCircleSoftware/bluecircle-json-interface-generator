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
 */
package com.bluecirclesoft.open.jigen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bluecirclesoft.open.jigen.Regexes;

/**
 * This class represents a TypeScript namespace. It may contain other namespaces, or declarations of other types
 */
public class Namespace implements Serializable {

	private final List<Namespace> namespaces = new ArrayList<>();

	private final List<JToplevelType> declarations = new ArrayList<>();

	private final List<Endpoint> endpoints = new ArrayList<>();

	private String name;

	private Namespace containingNamespace;

	public Namespace(Namespace containingNamespace, String name) {
		this.containingNamespace = containingNamespace;
		this.name = name;
	}

	private Namespace() {
	}

	public static Namespace namespacifyModel(Model model, boolean stripCommonNamespaces) {
		Namespace top = new Namespace();

		for (JType thing : model.getInterfaces()) {
			if (thing instanceof JToplevelType) {
				JToplevelType tlType = (JToplevelType) thing;
				String name = tlType.getName();
				String[] brokenName = Regexes.DOT.split(name);
				String finalName = brokenName[brokenName.length - 1];
				Namespace containingName = top;
				for (int i = 0; i < brokenName.length - 1; i++) {
					containingName = containingName.findSubNamespace(brokenName[i]);
				}
				tlType.setName(finalName);
				containingName.addDeclaration(tlType);
			}
		}

		for (Endpoint endpoint : model.getEndpoints()) {
			String name = endpoint.getId();
			String[] brokenName = Regexes.DOT.split(name);
			String finalName = brokenName[brokenName.length - 1];
			Namespace containingName = top;
			for (int i = 0; i < brokenName.length - 1; i++) {
				containingName = containingName.findSubNamespace(brokenName[i]);
			}
			endpoint.setId(finalName);
			containingName.addEndpoint(endpoint);
		}

		if (stripCommonNamespaces) {
			// strip common namespaces
			while (top.getNamespaces().size() == 1 && top.isDeclarationsEmpty()) {
				top = top.getNamespaces().get(0);
			}

			top.setContainingNamespace(null);
		}

		// Sort namespaces for stability of output
		top.sort();

		return top;
	}

	private Namespace findSubNamespace(String name) {
		for (Namespace thing : namespaces) {
			if (thing != null && thing.getName().equals(name)) {
				return thing;
			}
		}
		Namespace newNamespace = new Namespace(this, name);
		namespaces.add(newNamespace);
		return newNamespace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Namespace> getNamespaces() {
		return namespaces;
	}

	public Namespace getContainingNamespace() {
		return containingNamespace;
	}

	private void setContainingNamespace(Namespace containingNamespace) {
		this.containingNamespace = containingNamespace;
	}

	public Iterable<? extends JToplevelType> getDeclarations() {
		return declarations;
	}

	private void addDeclaration(JToplevelType tlType) {
		declarations.add(tlType);
		tlType.setContainingNamespace(this);
	}

	private boolean isDeclarationsEmpty() {
		return declarations.isEmpty();
	}

	/**
	 * Sort all my sub-elements
	 */
	private void sort() {
		namespaces.sort(Comparator.comparing(Namespace::getName));
		endpoints.sort(Comparator.comparing(Endpoint::getId));
		declarations.sort(Comparator.comparing(JToplevelType::getName));
		for (Namespace subNamespace : namespaces) {
			subNamespace.sort();
		}
	}

	private void addEndpoint(Endpoint endpoint) {
		endpoints.add(endpoint);
		endpoint.setNamespace(this);
	}


	public List<Endpoint> getEndpoints() {
		return endpoints;
	}


	public String conjoin(String separator) {
		String name = this.getName();
		Namespace containingNamespace = this.getContainingNamespace();
		if (containingNamespace == null) {
			if (StringUtils.isBlank(name)) {
				return "";
			} else {
				return name;
			}
		} else {
			String parent = containingNamespace.conjoin(separator);
			if (StringUtils.isBlank(parent)) {
				return name;
			} else {
				return parent + separator + name;
			}
		}
	}
}

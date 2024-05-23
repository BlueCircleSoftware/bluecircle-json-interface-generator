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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.bluecirclesoft.open.jigen.Regexes;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a TypeScript namespace. It may contain other namespaces, or declarations of other types
 */
public class Namespace implements Serializable {

	@Getter
	private final List<Namespace> namespaces = new ArrayList<>();

	private final List<JToplevelType> declarations = new ArrayList<>();

	@Getter
	private final List<Endpoint> endpoints = new ArrayList<>();

	private File file;

	@Setter
	@Getter
	private String name;

	@Getter
	private Namespace containingNamespace;

	public Namespace(Namespace containingNamespace, String name) {
		this(containingNamespace, name, null);
	}

	public Namespace(Namespace containingNamespace, String name, File file) {
		this.containingNamespace = containingNamespace;
		this.name = name;
		this.file = file;
		if (file != null) {
			if (!file.exists()) {
				throw new RuntimeException("File " + file.getAbsolutePath() + " does not exist");
			}
		}
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

	public File conjoin(File defaultFolder, String separator) {
		return conjoin(defaultFolder, separator, null);
	}

	public File conjoin(File defaultFolder, String separator, String suffix) {
		if (file != null) {
			return file;
		}

		Deque<Namespace> lineage = getContainerLineage();

		String joinedName = lineage.stream().map(n -> n.name).collect(Collectors.joining(separator));
		if (suffix != null) {
			joinedName = joinedName + suffix;
		}
		return new File(defaultFolder, joinedName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Namespace)) {
			return false;
		}

		Namespace namespace = (Namespace) o;

		if (!getName().equals(namespace.getName())) {
			return false;
		}
		return getContainingNamespace() != null ? getContainingNamespace().equals(namespace.getContainingNamespace()) :
				namespace.getContainingNamespace() == null;
	}

	@Override
	public int hashCode() {
		int result = getName().hashCode();
		result = 31 * result + (getContainingNamespace() != null ? getContainingNamespace().hashCode() : 0);
		return result;
	}

	public String conjoin(String separator) {
		Deque<Namespace> lineage = getContainerLineage();

		return lineage.stream().map(n -> n.name).collect(Collectors.joining(separator));
	}

	private Deque<Namespace> getContainerLineage() {
		Deque<Namespace> lineage = new ArrayDeque<>();
		Namespace ns = this;
		while (ns != null) {
			if (StringUtils.isNotBlank(ns.name)) {
				lineage.addFirst(ns);
			}
			ns = ns.containingNamespace;
		}
		return lineage;
	}
}

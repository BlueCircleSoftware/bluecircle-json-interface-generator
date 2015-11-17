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

package com.bluecirclesoft.open.jigen.output.typeScript;

import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.JTypeVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO document me
 */
public class Namespace {

	private String name;

	private final List<Namespace> namespaces = new ArrayList<>();

	private final List<JType> declarations = new ArrayList<>();

	public Namespace(String name) {
		this.name = name;
	}

	public Namespace() {
	}

	public Namespace findSubNamespace(String name) {
		for (Namespace thing : namespaces) {
			if (thing != null && thing.getName().equals(name)) {
				return thing;
			}
		}
		Namespace newNamespace = new Namespace(name);
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

	public List<JType> getDeclarations() {
		return declarations;
	}

}

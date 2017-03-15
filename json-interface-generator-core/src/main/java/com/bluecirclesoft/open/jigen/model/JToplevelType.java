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

import org.apache.commons.lang3.StringUtils;

/**
 * Base class for types that are defined at top level (namely, objects and enums).
 */
public abstract class JToplevelType extends JType {

	private Namespace containingNamespace;

	/**
	 * Get the namespace that this type is defined in.
	 *
	 * @return the namespace
	 */
	public Namespace getContainingNamespace() {
		return containingNamespace;
	}

	public void setContainingNamespace(Namespace containingNamespace) {
		this.containingNamespace = containingNamespace;
	}

	abstract public String getName();

	public String getReference() {
		String namespace = "??";
		if (containingNamespace != null) {
			namespace = getContainingNamespace().getReference();
		}
		if (StringUtils.isBlank(namespace)) {
			return getName();
		} else {
			return namespace + "." + getName();
		}
	}

	abstract public void setName(String name);

}

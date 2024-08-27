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

/**
 * The base JSON type class - all things that are represented in JSON, extend this class.
 */
public abstract class JType implements Serializable {

	// TODO debugging only

	public static final List<JType> createdTypes = new ArrayList<>();

	public abstract <T> T accept(JTypeVisitor<T> visitor);

	public abstract void accept(JTypeVisitorVoid visitor);

	public abstract boolean needsWrapping();

	public JType getStripped() {
		return this;
	}

	public boolean isConstructible() {
		return false;
	}

	public boolean canBeNull() {
		return false;
	}

	public boolean canBeUndefined() {
		return false;
	}

	public boolean hasTypeVariables() {
		return false;
	}

	public List<JTypeVariable> getTypeVariables() {
		return new ArrayList<>();
	}

	/**
	 * Get the namespace that this type is defined in.
	 *
	 * @return the namespace, or null if there is no namespace (e.g., 'unknown', 'number', type variable, etc.)
	 */
	public Namespace getContainingNamespace() {
		return null;
	}

	/**
	 * Can be specialized, i.e., can have {@code <T>} after it
	 *
	 * @return tes or no
	 */
	public boolean isSpecializable() {
		return true;
	}
}

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

/**
 * TODO document me
 */
public class JArray extends JType {

	private JType indexType;

	private JType elementType;

	public JType getIndexType() {
		return indexType;
	}

	public void setIndexType(JType indexType) {
		this.indexType = indexType;
	}

	public JType getElementType() {
		return elementType;
	}

	public void setElementType(JType elementType) {
		this.elementType = elementType;
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("indexType", indexType)
				.append("elementType", elementType)
				.toString();
	}
}

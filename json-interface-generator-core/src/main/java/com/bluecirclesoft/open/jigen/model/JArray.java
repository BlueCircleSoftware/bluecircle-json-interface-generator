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

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Corresponds to TypeScript {@code Array<elementType>}.
 */
@Setter
@Getter
public class JArray extends JType {

	/**
	 * TODO this is always numeric in the code, I think it should be always numeric. Remove.
	 */
	private JType indexType;

	private JType elementType;

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public void accept(JTypeVisitorVoid visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("indexType", indexType).append("elementType", elementType).toString();
	}

	@Override
	public boolean needsWrapping() {
		return true;
	}

	@Override
	public boolean hasTypeVariables() {
		return elementType.hasTypeVariables();
	}

	@Override
	public List<JTypeVariable> getTypeVariables() {
		return elementType.getTypeVariables();
	}
}

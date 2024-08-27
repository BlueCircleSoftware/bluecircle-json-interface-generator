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

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Corresponds to a map - if 'valueType' is V, then this represents the TypeScript type {@code {[key:string]:V} }
 */
@Setter
@Getter
public class JMap extends JType {

	private JType valueType;

	public JMap() {
	}

	public JMap(JType valueType) {
		this.valueType = valueType;
	}

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
		return new ToStringBuilder(this).append("valueType", valueType).toString();
	}

	@Override
	public boolean needsWrapping() {
		return true;
	}

	@Override
	public boolean hasTypeVariables() {
		return valueType.hasTypeVariables();
	}

	@Override
	public List<JTypeVariable> getTypeVariables() {
		return valueType.getTypeVariables();
	}
}

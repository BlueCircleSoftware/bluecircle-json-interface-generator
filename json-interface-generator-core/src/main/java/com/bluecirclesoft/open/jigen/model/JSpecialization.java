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
public class JSpecialization extends JType {

	private JType base;

	private JType[] parameters;

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JType getBase() {
		return base;
	}

	public void setBase(JType base) {
		this.base = base;
	}

	public JType[] getParameters() {
		return parameters;
	}

	public void setParameters(JType[] parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("base", base)
				.append("parameters", parameters)
				.toString();
	}

	@Override
	public boolean needsWrapping() {
		return base.needsWrapping();
	}
}

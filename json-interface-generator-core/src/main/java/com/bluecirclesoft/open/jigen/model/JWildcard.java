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

import java.util.ArrayList;
import java.util.List;

/**
 * TODO document me
 */
public class JWildcard extends JType {

	private final List<JType> upperBounds = new ArrayList<>();

	private final List<JType> lowerBounds = new ArrayList<>();

	public List<JType> getUpperBounds() {
		return upperBounds;
	}

	public List<JType> getLowerBounds() {
		return lowerBounds;
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean needsWrapping() {
		return false;
	}

	@Override
	public boolean hasTypeVariables() {
		for (JType bound : upperBounds) {
			if (bound.hasTypeVariables()) {
				return true;
			}
		}
		for (JType bound : lowerBounds) {
			if (bound.hasTypeVariables()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<JTypeVariable> getTypeVariables() {
		List<JTypeVariable> result = new ArrayList<>();
		for (JType bound : upperBounds) {
			result.addAll(bound.getTypeVariables());
		}
		for (JType bound : lowerBounds) {
			result.addAll(bound.getTypeVariables());
		}
		return result;
	}

	@Override
	public boolean isSpecializable() {
		return false;
	}
}

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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the TypeScript union type (i.e., A | B | C | ...)
 */
public class JUnionType extends JType {

	private final List<JType> members = new ArrayList<>();

	public List<JType> getMembers() {
		return members;
	}

	@Override
	public <T> T accept(JTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean needsWrapping() {
		for (JType member : getMembers()) {
			if (member.needsWrapping()) {
				return true;
			}
		}
		return false;
	}

	public JType getStripped() {
		JUnionType newType = new JUnionType();
		for (JType member : getMembers()) {
			if (!(member instanceof JNull)) {
				newType.getMembers().add(member);
			}
		}
		if (newType.getMembers().size() == 1) {
			return newType.getMembers().get(0);
		} else {
			return newType;
		}
	}

	@Override
	public boolean canBeNull() {
		for (JType member : getMembers()) {
			if (member.canBeNull()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canBeUndefined() {
		for (JType member : getMembers()) {
			if (member.canBeUndefined()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasTypeVariables() {
		for (JType member : getMembers()) {
			if (member.hasTypeVariables()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<JTypeVariable> getTypeVariables() {
		List<JTypeVariable> result = new ArrayList<>();
		for (JType member : getMembers()) {
			result.addAll(member.getTypeVariables());
		}
		return result;
	}

	@Override
	public boolean isSpecializable() {
		return false;
	}
}

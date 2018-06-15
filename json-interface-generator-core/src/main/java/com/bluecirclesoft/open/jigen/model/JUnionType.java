/*
 * Copyright 2017 Blue Circle Software, LLC
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

import java.util.ArrayList;
import java.util.List;

/**
 * TODO document me
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
		for (JType member : members) {
			if (member.needsWrapping()) {
				return true;
			}
		}
		return false;
	}

	public JUnionType getStripped() {
		JUnionType newType = new JUnionType();
		for (JType member : members) {
			if (!(member instanceof JNull)) {
				newType.getMembers().add(member);
			}
		}
		return newType;
	}

	@Override
	public boolean canBeNull() {
		for (JType member : members) {
			if (member.canBeNull()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canBeUndefined() {
		for (JType member : members) {
			if (member.canBeUndefined()) {
				return true;
			}
		}
		return false;
	}
}

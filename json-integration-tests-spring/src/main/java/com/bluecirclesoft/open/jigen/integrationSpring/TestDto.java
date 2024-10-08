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

package com.bluecirclesoft.open.jigen.integrationSpring;

import lombok.Getter;
import lombok.Setter;

/**
 * Simple DTO for use in classes generated by the TestCreator class
 */
@Setter
@Getter
public class TestDto {

	private String a = "";

	private String b = "";

	private String c = "";

	public void appendAll(String m) {
		a += m.toUpperCase();
		b += m.toUpperCase();
		c += m.toUpperCase();
	}

	public void append(TestDto t) {
		a += t.getA().toUpperCase();
		b += t.getB().toUpperCase();
		c += t.getC().toUpperCase();
	}

	@Override
	public String toString() {
		return "TestDto{" + "a='" + a + '\'' + ", b='" + b + '\'' + ", c='" + c + '\'' + '}';
	}
}

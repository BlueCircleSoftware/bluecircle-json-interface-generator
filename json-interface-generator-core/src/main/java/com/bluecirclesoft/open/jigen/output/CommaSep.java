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

package com.bluecirclesoft.open.jigen.output;

/**
 * Turn elements into a comma-separated string
 */
public class CommaSep {

	private final StringBuilder sb = new StringBuilder();

	private final String separator;

	private boolean needsComma = false;

	/**
	 * Create builder, with specified separator
	 *
	 * @param separator the separator
	 */
	public CommaSep(String separator) {
		this.separator = separator;
	}

	/**
	 * Create builder, with comma as separator
	 */
	public CommaSep() {
		this(",");
	}

	/**
	 * Add element
	 *
	 * @param s the element
	 */
	public void add(String s) {
		if (needsComma) {
			sb.append(separator);
		} else {
			needsComma = true;
		}
		sb.append(s);
	}

	/**
	 * Get separated string of elements.
	 *
	 * @return the string
	 */
	public String get() {
		return sb.toString();
	}
}

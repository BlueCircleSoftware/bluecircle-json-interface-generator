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
 */

package com.bluecirclesoft.open.jigen;

import java.util.regex.Pattern;

public final class Regexes {

	public static final Pattern COMMA_SEPARATOR = Pattern.compile("\\s+,\\s+");

	public static final Pattern EQUALS_SEPARATOR = Pattern.compile("\\s+=\\s+");

	public static final Pattern SPACE_SEPARATOR = Pattern.compile("\\s+");

	public static final Pattern DOT = Pattern.compile("\\.");

	private Regexes() {
	}
}

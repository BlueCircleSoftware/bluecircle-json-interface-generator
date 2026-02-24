/*
 * Copyright 2026 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.typescript;

import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

import java.util.Objects;

public class NameSafety {

	private static final Pattern SAFE_IDENTIFIER = Pattern.compile("[A-Za-z_$][A-Za-z0-9_$]*");

	public static String saveObjectName(String name) {
		if (Objects.equals(name, "Object")) {
			return "JavaObject";
		}
		return name;
	}

	public static boolean isSafeIdentifier(String name) {
		return name != null && SAFE_IDENTIFIER.matcher(name).matches();
	}

	public static String tsStringLiteral(String value) {
		return "\"" + StringEscapeUtils.escapeEcmaScript(value) + "\"";
	}

	public static String safePropertyName(String name) {
		if (isSafeIdentifier(name)) {
			return name;
		}
		return tsStringLiteral(name);
	}

	public static String safeAccessorName(String name) {
		if (isSafeIdentifier(name)) {
			return name;
		}
		return "[" + tsStringLiteral(name) + "]";
	}
}

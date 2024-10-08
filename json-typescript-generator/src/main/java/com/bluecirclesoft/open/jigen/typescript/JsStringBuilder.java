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

package com.bluecirclesoft.open.jigen.typescript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.JavaUnicodeEscaper;
import org.apache.commons.text.translate.LookupTranslator;

/**
 * <p>Build a JavaScript/TypeScript string by assembling a bunch of components, some of which may be literals, and some of which may be code
 * snippets.  Adjacent literal components will be concatenated into one large literal.
 * </p>
 * So, for example
 * {@code
 * JsStringBuilder jsb = new JsStringBuilder();
 * jsb.addCode("a");
 * jsb.addLiteral("b");
 * jsb.addLiteral("c");
 * jsb.addCode("d");
 * }
 * will produce the following output:
 * {@code
 * a+"bc"+d
 * }
 */
public class JsStringBuilder {

	private static final Map<CharSequence, CharSequence> myXlat = new HashMap<>();

	static {
		myXlat.put("\"", "\\\"");
		myXlat.put("\\", "\\\\");
	}

	private static final CharSequenceTranslator ESCAPE_ECMASCRIPT =
			new AggregateTranslator(new LookupTranslator(myXlat), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE),
					JavaUnicodeEscaper.outsideOf(32, 127));

	/**
	 * For each component, is it literal?
	 */
	private final List<Boolean> literalFlags = new ArrayList<>();

	/**
	 * The components
	 */
	private final List<String> components = new ArrayList<>();

	/**
	 * Add a literal component
	 *
	 * @param s the string
	 */
	void addLiteral(String s) {
		literalFlags.add(true);
		components.add(s);
	}

	/**
	 * Add a code snippet
	 *
	 * @param s the snippet
	 */
	void addCode(String s) {
		literalFlags.add(false);
		components.add(s);
	}

	/**
	 * Get the resulting string. If no components were added, it returns a representation of the empty string.
	 *
	 * @return a printable string
	 */
	public String get() {
		if (literalFlags.isEmpty()) {
			return "\"\"";
		}

		StringBuilder result = new StringBuilder();
		boolean lastWasLiteral = false;
		for (int i = 0; i < literalFlags.size(); i++) {
			boolean literal = literalFlags.get(i);
			String component = components.get(i);
			if (literal) {
				String escaped = ESCAPE_ECMASCRIPT.translate(component);
				if (lastWasLiteral) {
					result.append(escaped);
				} else {
					if (i > 0) {
						result.append(" + ");
					}
					result.append('"');
					result.append(escaped);
				}
			} else {
				if (lastWasLiteral) {
					result.append('"');
				}
				if (i > 0) {
					result.append(" + ");
				}
				result.append(component);
			}
			lastWasLiteral = literal;
		}
		if (lastWasLiteral) {
			result.append('"');
		}
		return result.toString();
	}

}

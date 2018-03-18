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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * TODO document me
 */
class OutputHandler {

	private PrintWriter writer;

	private int indentLevel = 0;

	OutputHandler(PrintWriter writer) {
		this.writer = writer;
	}

	void line() {
		writer.println();
	}

	void line(String str) {
		indent();
		writer.println(str);
	}

	private void indent(StringBuilder sb) {
		for (int i = 0; i < indentLevel; i++) {
			sb.append("    ");
		}
	}

	private void indent() {
		StringBuilder sb = new StringBuilder();
		indent(sb);
		writer.print(sb.toString());
	}

	void indentIn() {
		indentLevel++;
	}

	void indentOut() {
		indentLevel--;
	}


	void flush() {
		writer.flush();
	}

	void close() {
		writer.close();
	}

	void writeResource(String resourcePath) {
		InputStream fileResource = OutputHandler.class.getResourceAsStream(resourcePath);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileResource))) {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

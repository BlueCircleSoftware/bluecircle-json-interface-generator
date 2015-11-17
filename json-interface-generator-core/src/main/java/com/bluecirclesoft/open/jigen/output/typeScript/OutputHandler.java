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

package com.bluecirclesoft.open.jigen.output.typeScript;

import java.io.PrintWriter;

/**
 * TODO document me
 */
public class OutputHandler {

	PrintWriter writer;

	private int indentLevel = 0;

	public OutputHandler(PrintWriter writer) {
		this.writer = writer;
	}

	public void line() {
		writer.println();
	}

	public void line(String str) {
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


	public void flush() {
		writer.flush();
	}

	public void close() {
		writer.close();
	}
}

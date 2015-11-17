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

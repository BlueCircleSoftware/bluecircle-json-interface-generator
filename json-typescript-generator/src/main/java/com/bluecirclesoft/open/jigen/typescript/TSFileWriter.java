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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.bluecirclesoft.open.jigen.model.Namespace;

/**
 * TODO document me
 */
class TSFileWriter {

	public static final Namespace JIG_NAMESPACE = new Namespace(null, "jsonInterfaceGenerator");

	private final PrintWriter writer;

	private final File file;

	private final StringWriter stringWriter;

	private final Map<String, String> imports = new TreeMap<>();

	private final OutputController outputController;

	private final boolean doImports;

	private int indentLevel = 0;

	TSFileWriter(OutputController outputController, File file) {
		this.file = file;
		this.stringWriter = new StringWriter();
		this.writer = new PrintWriter(this.stringWriter);
		this.outputController = outputController;
		this.doImports = outputController.needsImports();
	}

	static String relativize(Path fromNamespacePath, Path toNamespacePath) {
		// if given /a/a.ts and /a/b.ts, relativize will return "../b.ts". Below is a workaround.
		Path fromNamespaceParent = fromNamespacePath.normalize().getParent();
		FileSystem defaultFs = FileSystems.getDefault();
		if (fromNamespaceParent == null) {
			fromNamespaceParent = defaultFs.getPath(".");
		}
		Path toNamespaceParent = toNamespacePath.normalize().getParent();
		if (toNamespaceParent == null) {
			toNamespaceParent = defaultFs.getPath(".");
		}
		if (Objects.equals(fromNamespaceParent, toNamespaceParent)) {
			return defaultFs.getPath(".", toNamespacePath.getFileName().toString()).toString();
		} else {
			String relPathStr = fromNamespaceParent.relativize(toNamespacePath).toString();
			// webpack wants relative paths to start with ./
			if (!relPathStr.startsWith(".")) {
				relPathStr = "." + File.separator + relPathStr;
			}
			return relPathStr;
		}
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

	void close() throws IOException {
		writer.flush();
		writer.close();
		String content = stringWriter.toString();
		if (content.trim().length() > 0 || imports.size() > 0) {
			try (FileWriter fileWriter = new FileWriter(file)) {
				for (Map.Entry<String, String> entry : imports.entrySet()) {
					if (!StringUtils.isBlank(entry.getValue())) {
						String dest = entry.getValue();
						if (dest.endsWith(".ts")) {
							dest = dest.substring(0, dest.length() - 3);
						}
						fileWriter.write("import * as " + entry.getKey() + " from \"" + dest + "\";");
						fileWriter.write(System.lineSeparator());
					}
				}
				fileWriter.write(content);
			}
		}
	}

	void writeResource(String resourcePath, Function<String, String> substitution) {
		InputStream fileResource = TSFileWriter.class.getResourceAsStream(resourcePath);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileResource))) {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				if (substitution != null) {
					line(substitution.apply(line));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void addImport(String namespaceLabel, Namespace fromNamespace, Namespace toNamespace) {
		if (!doImports) {
			return;
		}
		if (imports.containsKey(namespaceLabel)) {
			return;
		}
		Path fromNamespacePath = outputController.getFile(fromNamespace).toPath();
		Path toNamespacePath = outputController.getFile(toNamespace).toPath();
		String relative = relativize(fromNamespacePath, toNamespacePath);
		imports.put(namespaceLabel, relative);
	}

	public Namespace getJIGNamespace() {
		return JIG_NAMESPACE;
	}
}

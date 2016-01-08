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

import com.bluecirclesoft.open.jigen.model.JToplevelType;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.output.OutputProducer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TODO document me
 */
public class TypeScriptProducer implements OutputProducer {

	private final File outputFile;

	private OutputHandler writer;

	public TypeScriptProducer(File outputFile) {
		this.outputFile = outputFile;
	}

	public TypeScriptProducer(PrintWriter writer) {
		outputFile = null;
		this.writer = new OutputHandler(writer);
	}

	@Override
	public void output(Model model) throws IOException {
		Namespace ns = namespacifyModel(model);
		start();
		try {
			outputNamespace(ns);
		} finally {
			if (writer != null) {
				writer.flush();
				if (writer != null && outputFile != null) {
					writer.close();
				}
			}
		}
	}

	private void outputNamespace(Namespace namespace) {
		if (namespace.getName() != null) {
			writer.line();
			writer.line("export namespace " + namespace.getName() + " {");
			writer.indentIn();
		}
		for (JType intf : namespace.getDeclarations()) {
			intf.accept(new TypeDeclarationProducer(writer));
		}
		for (Namespace subNamespace : namespace.getNamespaces()) {
			outputNamespace(subNamespace);
		}
		if (namespace.getName() != null) {
			writer.indentOut();
			writer.line("}");
		}

	}

	private void start() throws IOException {
		if (outputFile != null) {
			File outputDir = outputFile.getParentFile();
			if (!outputDir.exists()) {
				if (!outputDir.mkdirs()) {
					throw new RuntimeException(
							"Could not create folder " + outputDir.getAbsolutePath());
				}
			}
			writer = new OutputHandler(new PrintWriter(new FileWriter(outputFile)));
		}
		writer.line("/// include(\"jquery.d.ts\")");
		writer.line();
	}

	private Namespace namespacifyModel(Model model) {
		Namespace top = new Namespace();

		for (JType thing : model.getInterfaces()) {
			if (thing instanceof JToplevelType) {
				JToplevelType tlType = (JToplevelType) thing;
				String[] brokenName = tlType.getName().split("\\.");
				String finalName = brokenName[brokenName.length - 1];
				Namespace containingName = top;
				for (int i = 0; i < brokenName.length - 1; i++) {
					containingName = containingName.findSubNamespace(brokenName[i]);
				}
				tlType.setName(finalName);
				containingName.getDeclarations().add(tlType);
			}
		}

		// strip common namespaces
		while (top.getNamespaces().size() == 1 && top.getDeclarations().isEmpty()) {
			top = top.getNamespaces().get(0);
		}

		return top;
	}


}

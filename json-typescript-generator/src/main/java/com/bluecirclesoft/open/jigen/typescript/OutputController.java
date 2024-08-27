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
 *
 */

package com.bluecirclesoft.open.jigen.typescript;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.bluecirclesoft.open.jigen.model.Namespace;
import lombok.Getter;

/**
 * Produces correct OutputHandlers based on the selected options.
 */
public class OutputController {

	private final Options options;

	private final TSFileWriter theOneHandler;

	private final File outputDir;

	private final File outputFileAbsolute;

	@Getter
	private final Namespace jigNamespace;

	public OutputController(Options options) {
		this.options = options;
		this.jigNamespace = new Namespace(null, "jsonInterfaceGenerator",
				options.getHeaderLocation() == null ? null : new File(options.getHeaderLocation()));
		outputFileAbsolute = new File(options.getOutputFile()).getAbsoluteFile();
		switch (options.getOutputStructure()) {
			case NAMESPACES:
				outputDir = outputFileAbsolute.getParentFile();
				theOneHandler = new TSFileWriter(this, outputFileAbsolute, jigNamespace);
				break;
			case FILES_IN_ONE_FOLDER:
			case FILES_IN_TREE: {
				outputDir = outputFileAbsolute;
				theOneHandler = null;
				break;
			}
			default:
				throw new RuntimeException("Unhandled output structure " + options.getOutputStructure());
		}
		if (!outputDir.exists()) {
			if (!outputDir.mkdirs()) {
				throw new RuntimeException("Could not create folder " + outputDir.getAbsolutePath());
			}
		}
	}

	public TSFileWriter getNamespaceHandler(Namespace namespace) {
		if (Objects.requireNonNull(options.getOutputStructure()) == OutputStructure.NAMESPACES) {
			return theOneHandler;
		}
		return new TSFileWriter(this, getFile(namespace), jigNamespace);
	}

	public void close(TSFileWriter writer) throws IOException {
		switch (options.getOutputStructure()) {
			case NAMESPACES:
				break;
			case FILES_IN_ONE_FOLDER:
			case FILES_IN_TREE:
				writer.close();
				break;
			default:
				throw new RuntimeException("Unhandled output structure " + options.getOutputStructure());
		}
	}

	public void finish() throws IOException {
		switch (options.getOutputStructure()) {
			case NAMESPACES:
				theOneHandler.close();
				break;
			case FILES_IN_ONE_FOLDER:
			case FILES_IN_TREE:
				break;
			default:
				throw new RuntimeException("Unhandled output structure " + options.getOutputStructure());
		}
	}

	public File getFile(Namespace namespace) {
		switch (options.getOutputStructure()) {
			case NAMESPACES:
				return outputFileAbsolute;
			case FILES_IN_ONE_FOLDER:
				return namespace.conjoin(outputDir, "_", ".ts");
			case FILES_IN_TREE:
				File treeName = namespace.conjoin(outputDir, File.separator, ".ts");
				treeName.getParentFile().mkdirs();
				return treeName;
			default:
				throw new RuntimeException("Unhandled output structure " + options.getOutputStructure());
		}
	}

	public boolean needsImports() {
		switch (options.getOutputStructure()) {
			case NAMESPACES:
				return false;
			case FILES_IN_ONE_FOLDER:
			case FILES_IN_TREE:
				return true;
			default:
				throw new RuntimeException("Unhandled output structure " + options.getOutputStructure());
		}
	}

	public String getReferencePrefix(Namespace currentNamespace, Namespace referencedNamespace) {
		switch (options.getOutputStructure()) {
			case NAMESPACES:
				if (Objects.equals(currentNamespace, referencedNamespace)) {
					return "";
				} else {
					return referencedNamespace.conjoin(".");
				}
			case FILES_IN_ONE_FOLDER:
			case FILES_IN_TREE:
				if (Objects.equals(currentNamespace, referencedNamespace)) {
					return "";
				} else {
					return referencedNamespace.conjoin("_");
				}
			default:
				throw new RuntimeException("Unhandled output structure " + options.getOutputStructure());
		}
	}
}

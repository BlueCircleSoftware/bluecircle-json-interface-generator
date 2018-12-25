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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.bluecirclesoft.open.jigen.model.JToplevelType;
import com.bluecirclesoft.open.jigen.model.Namespace;

/**
 * TODO document me
 */
public class ReferenceGenerator {

	private final Namespace referenceLocation;

	private final Options options;

	private final TSFileWriter writer;

	public ReferenceGenerator(Namespace referenceLocation, TSFileWriter writer, Options options) {
		this.referenceLocation = referenceLocation;
		this.options = options;
		this.writer = writer;
	}

	public String makeReference(JToplevelType type) {
		if (type.getContainingNamespace() == null || Objects.equals(type.getContainingNamespace(), referenceLocation)) {
			return type.getName();
		}
		switch (options.getOutputStructure()) {
			case NAMESPACES:
				return makeReferenceNamespaces(type);
			case FILES_IN_ONE_FOLDER:
				return makeReferenceOneFolder(type);
			case FILES_IN_TREE:
				return makeReferenceTree(type);
			default:
				throw new RuntimeException("Unhandled output structure " + options.getOutputStructure());
		}
	}

	private String makeReferenceTree(JToplevelType type) {
		String namespaceLabel = type.getContainingNamespace().conjoin("_");
		writer.addImport(namespaceLabel, referenceLocation, type.getContainingNamespace());
		return namespaceLabel + "." + type.getName();
	}

	private String makeReferenceOneFolder(JToplevelType type) {
		String namespaceLabel = type.getContainingNamespace().conjoin("_");
		writer.addImport(namespaceLabel, referenceLocation, type.getContainingNamespace());
		return namespaceLabel + "." + type.getName();
	}

	private String makeReferenceNamespaces(JToplevelType type) {
		String namespace = "??";
		Namespace typeNamespace = type.getContainingNamespace();
		if (typeNamespace != null) {
			namespace = typeNamespace.conjoin(".");
		}
		String typeName = type.getName();
		if (StringUtils.isBlank(namespace)) {
			return typeName;
		} else {
			return namespace + "." + typeName;
		}
	}
}

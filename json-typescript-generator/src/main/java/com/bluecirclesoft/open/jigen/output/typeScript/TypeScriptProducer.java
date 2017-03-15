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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.EndpointParameter;
import com.bluecirclesoft.open.jigen.model.HttpMethod;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.model.Namespace;
import com.bluecirclesoft.open.jigen.output.OutputProducer;

/**
 * Generate TypeScript from a REST model.
 */
public class TypeScriptProducer implements OutputProducer {

	private final File outputFile;

	private final String typingsPath;

	private OutputHandler writer;

	private boolean produceAccessors = true;

	private boolean produceAccessorFunctionals = true;

	private boolean stripCommonNamespaces = false;

	public boolean isProduceAccessors() {
		return produceAccessors;
	}

	public void setProduceAccessors(boolean produceAccessors) {
		this.produceAccessors = produceAccessors;
	}

	public boolean isProduceAccessorFunctionals() {
		return produceAccessorFunctionals;
	}

	public void setProduceAccessorFunctionals(boolean produceAccessorFunctionals) {
		this.produceAccessorFunctionals = produceAccessorFunctionals;
	}

	public boolean isStripCommonNamespaces() {
		return stripCommonNamespaces;
	}

	public void setStripCommonNamespaces(boolean stripCommonNamespaces) {
		this.stripCommonNamespaces = stripCommonNamespaces;
	}

	public TypeScriptProducer(File outputFile, String typingsPath) {
		this.outputFile = outputFile;
		this.typingsPath = typingsPath;
	}

	public TypeScriptProducer(PrintWriter writer, String typingsPath) {
		outputFile = null;
		this.writer = new OutputHandler(writer);
		this.typingsPath = typingsPath;
	}

	@Override
	public void output(Model model) throws IOException {
		Namespace ns = Namespace.namespacifyModel(model, stripCommonNamespaces);
		start();
		try {
			outputNamespace(ns, true);
			writer.line();
			writer.line("export {};");
		} finally {
			if (writer != null) {
				writer.flush();
				if (writer != null && outputFile != null) {
					writer.close();
				}
			}
		}
	}

	private void outputEndpoints(Namespace namespace) {
		for (Endpoint endpoint : namespace.getEndpoints()) {
			final String name = endpoint.getId();
			//			writer.line("// Processing " + endpoint);
			StringBuilder parameterList = new StringBuilder();
			boolean needsComma[] = {false};
			for (EndpointParameter parameter : endpoint.getParameters()) {
				addParameter(parameterList, needsComma, parameter.getCodeName(), parameter.getType());
			}
			Map<EndpointParameter.NetworkType, List<EndpointParameter>> sortedParams = sortParameters(endpoint.getParameters());
			String url = "'" + endpoint.getPathTemplate() + "'";
			List<EndpointParameter> urlParams = sortedParams.get(EndpointParameter.NetworkType.PATH);
			if (urlParams != null && !urlParams.isEmpty()) {
				for (EndpointParameter param : urlParams) {
					url = url.replace("{" + param.getNetworkName() + "}", "' + encodeURI(" + param.getCodeName() + ") + '");
				}
			}
			addParameter(parameterList, needsComma, "options",
					"jsonInterfaceGenerator" + ".JsonOptions<" + endpoint.getResponseBody().accept(new TypeUsageProducer()) + ">");
			writer.line("export function " + name + "(" + parameterList.toString() + ") : void {");
			writer.indentIn();
			if (endpoint.getMethod() == HttpMethod.POST) {
				// adding body parameter
				List<EndpointParameter> bodyParams = sortedParams.get(EndpointParameter.NetworkType.BODY);
				if (bodyParams != null && bodyParams.size() > 0) {
					writer.line("const submitData = JSON.stringify(" + bodyParams.get(0).getCodeName() + ");");
				} else {
					writer.line("const submitData = null;");
				}
			} else {
				// adding query parameters
				List<EndpointParameter> params = sortedParams.get(EndpointParameter.NetworkType.QUERY);
				writer.line("const submitData = {");
				writer.indentIn();
				if (params != null) {
					int plen = params.size();
					for (int i = 0; i < plen; i++) {
						EndpointParameter p = params.get(i);
						writer.line("'" + p.getNetworkName() + "': " + p.getCodeName() + (i == plen - 1 ? "" : ","));
					}
				}
				writer.indentOut();
				writer.line("};");
			}
			writer.line("$.ajax(jsonInterfaceGenerator.ajaxUrlPrefix + " + url + ", {");
			writer.indentIn();
			writer.line("method: '" + endpoint.getMethod().name() + "',");
			writer.line("contentType: \"application/json; charset=utf-8\",");
			writer.line("data: submitData,");
			writer.line("complete: options.complete,");
			writer.line("error: options.error,");
			writer.line("success: options.success");
			writer.indentOut();
			writer.line("});");
			writer.indentOut();
			writer.line("}");

		}
	}

	private Map<EndpointParameter.NetworkType, List<EndpointParameter>> sortParameters(List<EndpointParameter> parameters) {
		Map<EndpointParameter.NetworkType, List<EndpointParameter>> result = new HashMap<>();
		for (EndpointParameter parameter : parameters) {
			List<EndpointParameter> list = result.get(parameter.getNetworkType());
			if (list == null) {
				list = new ArrayList<>();
				result.put(parameter.getNetworkType(), list);
			}
			list.add(parameter);
		}
		return result;
	}

	private void addParameter(StringBuilder parameterList, boolean[] needsComma, String name, JType type) {
		addParameter(parameterList, needsComma, name, type.accept(new TypeUsageProducer()));
	}

	private void addParameter(StringBuilder parameterList, boolean[] needsComma, String name, String type) {
		if (needsComma[0]) {
			parameterList.append(", ");
		} else {
			needsComma[0] = true;
		}
		parameterList.append(name);
		parameterList.append(" : ");
		parameterList.append(type);
	}

	private void outputNamespace(Namespace namespace, boolean top) {
		if (namespace.getName() != null) {
			writer.line();
			// top-level namespaces should not get 'export' sub-namespaces should. Vagaries of JavaScript
			writer.line("export namespace " + namespace.getName() + " {");
			writer.indentIn();
		}
		for (JType intf : namespace.getDeclarations()) {
			intf.accept(new TypeDeclarationProducer(this, writer));
		}
		outputEndpoints(namespace);
		for (Namespace subNamespace : namespace.getNamespaces()) {
			outputNamespace(subNamespace, false);
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
					throw new RuntimeException("Could not create folder " + outputDir.getAbsolutePath());
				}
			}
			writer = new OutputHandler(new PrintWriter(new FileWriter(outputFile)));
		}
		writer.line("/// <reference path=\"" + typingsPath + "\" />\n");
		writer.writeResource("/header.ts");
		writer.line();
	}

}

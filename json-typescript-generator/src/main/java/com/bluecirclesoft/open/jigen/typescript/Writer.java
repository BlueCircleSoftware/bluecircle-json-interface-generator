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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.CodeProducer;
import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.EndpointParameter;
import com.bluecirclesoft.open.jigen.model.HttpMethod;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.model.Namespace;
import com.bluecirclesoft.open.jigen.model.ValidEndpointResponse;

/**
 * Generate TypeScript from a REST model.
 */
public class Writer implements CodeProducer<Options> {

	private static final Logger log = LoggerFactory.getLogger(Writer.class);

	private OutputHandler writer;

	private boolean produceAccessorFunctionals = false;

	private Options options;

	public boolean isProduceAccessorFunctionals() {
		return produceAccessorFunctionals;
	}

	public void setProduceAccessorFunctionals(boolean produceAccessorFunctionals) {
		this.produceAccessorFunctionals = produceAccessorFunctionals;
	}

	public boolean isStripCommonNamespaces() {
		return options.isStripCommonPackages();
	}

	public boolean isProduceImmutables() {
		return options.isProduceImmutables();
	}

	public boolean isTreatNullAsUndefined() {
		return options.isNullIsUndefined();
	}

	public Writer() {
	}

	Writer(PrintWriter writer) {
		this.writer = new OutputHandler(writer);
	}

	@Override
	public Class<Options> getOptionsClass() {
		return Options.class;
	}

	@Override
	public void acceptOptions(Options options, List<String> errors) {
		this.options = options;
		if (options.getOutputFile() == null) {
			errors.add("Output file is required.");
		}
	}

	@Override
	public void output(Model model) throws IOException {
		Namespace ns = Namespace.namespacifyModel(model, isStripCommonNamespaces());
		start();
		try {
			outputNamespace(ns);
			writer.line();
			writer.line("export {};");
		} finally {
			if (writer != null) {
				writer.flush();
				if (writer != null && getOutputFile() != null) {
					writer.close();
				}
			}
		}
	}

	private void outputEndpoints(Namespace namespace) {
		for (Endpoint endpoint : namespace.getEndpoints()) {
			ValidEndpointResponse validity = endpoint.isValid();
			if (validity.ok) {
				writeEndpoint(endpoint);
			} else {
				log.warn("Could not create caller for endpoint {}:", endpoint);
				for (String msg : validity.problems) {
					log.warn("endpoint problem: {}", msg);
				}
			}
		}
	}

	private void writeEndpoint(Endpoint endpoint) {
		final String name = endpoint.getId();

		// create parameter list for function declaration
		StringBuilder parameterList = new StringBuilder();
		boolean needsComma[] = {false};
		for (EndpointParameter parameter : endpoint.getParameters()) {
			addParameter(parameterList, needsComma, parameter.getCodeName(), parameter.getType());
		}
		addParameter(parameterList, needsComma, "options", "jsonInterfaceGenerator" + ".JsonOptions<" +
				endpoint.getResponseBody().accept(new TypeUsageProducer(null, isTreatNullAsUndefined())) + ">");
		writer.line("export function " + name + "(" + parameterList.toString() + ") : void {");
		writer.indentIn();

		// construct AJAX url, encoding any path params
		Map<EndpointParameter.NetworkType, List<EndpointParameter>> sortedParams = endpoint.getSortedParameters();
		final JsStringBuilder url = new JsStringBuilder();
		List<EndpointParameter> urlParams = sortedParams.get(EndpointParameter.NetworkType.PATH);
		handleUrlParams(endpoint.getPathTemplate(), url, urlParams);

		List<EndpointParameter> queryParams = sortedParams.get(EndpointParameter.NetworkType.QUERY);
		if (endpoint.getMethod() == HttpMethod.POST && queryParams != null && !queryParams.isEmpty()) {
			url.addLiteral("?");
			boolean needAnd = false;
			for (EndpointParameter param : queryParams) {
				if (needAnd) {
					url.addLiteral("&");
				} else {
					needAnd = true;
				}
				url.addLiteral(param.getNetworkName());
				url.addLiteral("=");
				url.addCode("encodeURI(String(" + param.getCodeName() + "))");
			}
		}

		// construct submission body
		List<EndpointParameter> bodyParams = sortedParams.get(EndpointParameter.NetworkType.BODY);
		boolean isBodyParam = bodyParams != null && bodyParams.size() > 0;
		switch (endpoint.getMethod()) {
			case POST:
			case PUT:
			case OPTIONS:
			case PATCH:
				// adding body parameter
				if (isBodyParam) {
					writer.line("const submitData = JSON.stringify(" + bodyParams.get(0).getCodeName() + ");");
				} else {
					List<EndpointParameter> params = sortedParams.get(EndpointParameter.NetworkType.FORM);
					createSubmitDataBodyFromParams(params);
				}
				break;
			case GET:
			case HEAD:
			case DELETE:
				// adding query parameters
				List<EndpointParameter> params = sortedParams.get(EndpointParameter.NetworkType.QUERY);
				createSubmitDataBodyFromParams(params);
				break;
			default:
				writer.line("const submitData = undefined;");
				break;
		}

		writer.line(
				"jsonInterfaceGenerator.callAjax(" + url.get() + ", \"" + endpoint.getMethod().name() + "\", submitData, " + isBodyParam +
						", options);");
		writer.indentOut();
		writer.line("}");
	}

	private static void handleUrlParams(String template, JsStringBuilder url, List<EndpointParameter> urlParams) {
		SortedMap<Integer, EndpointParameter> starts = new TreeMap<>();
		Map<Integer, Integer> ends = new HashMap<>();

		if (urlParams != null) {
			for (EndpointParameter param : urlParams) {
				String paramExpression = "{" + param.getNetworkName() + "}";
				int pos = template.indexOf(paramExpression);
				if (pos >= 0) {
					starts.put(pos, param);
					ends.put(pos, pos + paramExpression.length());
				}
			}
		}
		int curPos = 0;
		for (Map.Entry<Integer, EndpointParameter> entry : starts.entrySet()) {
			int newPos = entry.getKey();
			url.addLiteral(template.substring(curPos, newPos));
			url.addCode("encodeURI(String(" + entry.getValue().getCodeName() + "))");
			curPos = ends.get(newPos);
		}
		if (curPos < template.length()) {
			url.addLiteral(template.substring(curPos));
		}
	}

	private void createSubmitDataBodyFromParams(List<EndpointParameter> params) {
		writer.line("const submitData = {");
		writer.indentIn();
		if (params != null) {
			int numParams = params.size();
			for (int i = 0; i < numParams; i++) {
				EndpointParameter p = params.get(i);
				writer.line("'" + p.getNetworkName() + "': " + p.getCodeName() + (i == numParams - 1 ? "" : ","));
			}
		}
		writer.indentOut();
		writer.line("};");
	}

	private void addParameter(StringBuilder parameterList, boolean[] needsComma, String name, JType type) {
		addParameter(parameterList, needsComma, name, type.accept(new TypeUsageProducer(null, isTreatNullAsUndefined())));
	}

	private static void addParameter(StringBuilder parameterList, boolean[] needsComma, String name, String type) {
		if (needsComma[0]) {
			parameterList.append(", ");
		} else {
			needsComma[0] = true;
		}
		parameterList.append(name);
		parameterList.append(" : ");
		parameterList.append(type);
	}

	private void outputNamespace(Namespace namespace) {
		if (namespace.getName() != null) {
			writer.line();
			// top-level namespaces should not get 'export' sub-namespaces should. Vagaries of JavaScript
			writer.line("export namespace " + namespace.getName() + " {");
			writer.indentIn();
		}
		for (JType type : namespace.getDeclarations()) {
			type.accept(new TypeDeclarationProducer(this, writer, isProduceImmutables(), isTreatNullAsUndefined()));
		}
		outputEndpoints(namespace);
		for (Namespace subNamespace : namespace.getNamespaces()) {
			outputNamespace(subNamespace);
		}
		if (namespace.getName() != null) {
			writer.indentOut();
			writer.line("}");
		}

	}

	private void start() throws IOException {
		if (getOutputFile() != null) {
			File outputDir = getOutputFile().getAbsoluteFile().getParentFile();
			if (!outputDir.exists()) {
				if (!outputDir.mkdirs()) {
					throw new RuntimeException("Could not create folder " + outputDir.getAbsolutePath());
				}
			}
			writer = new OutputHandler(new PrintWriter(new FileWriter(getOutputFile())));
		}
		writer.writeResource("/header.ts");
		writer.line();

	}

	public File getOutputFile() {
		return new File(options.getOutputFile());
	}
}

/*
 * Copyright 2019 Blue Circle Software, LLC
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.CodeProducer;
import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.EndpointParameter;
import com.bluecirclesoft.open.jigen.model.HttpMethod;
import com.bluecirclesoft.open.jigen.model.JToplevelType;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.model.Namespace;
import com.bluecirclesoft.open.jigen.model.ValidEndpointResponse;

/**
 * Generate TypeScript from a REST model.
 */
public class Writer implements CodeProducer<Options> {

	private static final Logger log = LoggerFactory.getLogger(Writer.class);

	private boolean produceAccessorFunctionals = false;

	private Options options;

	private OutputController outputController;

	private TypeUsageProducer usageProducerNoSuffix;

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

	@Override
	public Class<Options> getOptionsClass() {
		return Options.class;
	}

	@Override
	public void acceptOptions(Options options, List<String> errors) {
		assert options != null : "options is null";
		this.options = options;
		if (options.getOutputFile() == null) {
			errors.add("Output file is required.");
		}
	}

	@Override
	public void output(Model model) throws IOException {
		assert options != null : "this.options is null - did you forget to call acceptOptions?";
		this.usageProducerNoSuffix =
				new TypeUsageProducer(options, TypeUsageProducer.WillBeSpecialized.YES, TypeUsageProducer.UseImmutableSuffix.NO);
		this.outputController = new OutputController(options);
		try {
			Namespace ns = Namespace.namespacifyModel(model, isStripCommonNamespaces());
			start();
			outputNamespace(ns);
		} finally {
			this.outputController.finish();
		}
	}

	private void outputEndpoints(Namespace namespace, TSFileWriter writer) {
		for (Endpoint endpoint : namespace.getEndpoints()) {
			ValidEndpointResponse validity = endpoint.isValid();
			if (validity.ok) {
				writeEndpoint(endpoint, writer);
			} else {
				log.warn("Could not create caller for endpoint {}:", endpoint);
				for (String msg : validity.problems) {
					log.warn("endpoint problem: {}", msg);
				}
			}
		}
	}

	private void writeEndpoint(Endpoint endpoint, TSFileWriter writer) {
		final String name = endpoint.getId();

		// create parameter list for function declaration
		StringBuilder parameterList = new StringBuilder();
		boolean needsComma[] = {false};
		for (EndpointParameter parameter : endpoint.getParameters()) {
			addParameter(parameterList, needsComma, parameter.getCodeName(),
					parameter.getType().accept(usageProducerNoSuffix.getProducer(endpoint.getNamespace(), writer)));
		}
		writer.addImport("jsonInterfaceGenerator", endpoint.getNamespace(), writer.getJIGNamespace());
		String returnType = endpoint.getResponseBody().accept(usageProducerNoSuffix.getProducer(endpoint.getNamespace(), writer));
		// save off constructed parameters
		StringBuilder savedParameterList = new StringBuilder(parameterList);
		boolean[] savedNeedsComma = Arrays.copyOf(needsComma, needsComma.length);
//		// callback style declaration
//		addParameter(parameterList, needsComma, "options", "jsonInterfaceGenerator" + ".JsonOptions<" + returnType + ">");
//		writer.line("export function " + name + "(" + parameterList.toString() + ") : void;");
		// promise style declaration
		parameterList = new StringBuilder(savedParameterList);
		needsComma = Arrays.copyOf(savedNeedsComma, savedNeedsComma.length);
		addParameter(parameterList, needsComma, "options?", "jsonInterfaceGenerator" + ".JsonOptions<" + returnType + ">");
		writer.line("export function " + name + "(" + parameterList.toString() + ") : Promise<" + returnType + ">;");
		// real implementation
		parameterList = new StringBuilder(savedParameterList);
		needsComma = Arrays.copyOf(savedNeedsComma, savedNeedsComma.length);
		addParameter(parameterList, needsComma, "options?", "jsonInterfaceGenerator" + ".JsonOptions<" + returnType + ">");
		writer.line("export function " + name + "(" + parameterList.toString() + ") : Promise<" + returnType + "> {");
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
		BodyType bodyType = BodyType.NONE;

		List<EndpointParameter> jsonBody = sortedParams.get(EndpointParameter.NetworkType.JSON_BODY);
		boolean isJsonBody = jsonBody != null && jsonBody.size() > 0;
		switch (endpoint.getMethod()) {
			case POST:
			case PUT:
			case OPTIONS:
			case PATCH:
				// adding body parameter
				if (isJsonBody) {
					bodyType = BodyType.JSON;
					writer.line("const submitData = JSON.stringify(" + jsonBody.get(0).getCodeName() + ");");
				} else {
					bodyType = BodyType.FORM;
					List<EndpointParameter> params = sortedParams.get(EndpointParameter.NetworkType.FORM);
					createSubmitDataBodyFromParams(params, writer);
				}
				break;
			case GET:
			case HEAD:
			case DELETE:
				// adding query parameters
				List<EndpointParameter> params = sortedParams.get(EndpointParameter.NetworkType.QUERY);
				createSubmitDataBodyFromParams(params, writer);
				break;
			default:
				writer.line("const submitData = undefined;");
				break;
		}

		String consumesValue;
		if (endpoint.getConsumes() == null) {
			consumesValue = "null";
		} else {
			consumesValue = '"' + endpoint.getConsumes() + '"';
		}
		writer.line(String.format("return jsonInterfaceGenerator.callAjax(%s, \"%s\", submitData, \"%s\", %s, options);", url.get(),
				endpoint.getMethod().name(), bodyType.jsValue, consumesValue));
		writer.indentOut();
		writer.line("}");
	}

	private void createSubmitDataBodyFromParams(List<EndpointParameter> params, TSFileWriter writer) {
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

	private void addParameter(StringBuilder parameterList, boolean[] needsComma, String name, JType type, Namespace currentNamespace,
	                          TSFileWriter writer) {
		addParameter(parameterList, needsComma, name, type.accept(usageProducerNoSuffix.getProducer(currentNamespace, writer)));
	}

	private void outputNamespace(Namespace namespace) throws IOException {
		TSFileWriter writer = outputController.getNamespaceHandler(namespace);
		try {
			boolean doingNamespace = namespace.getName() != null && options.getOutputStructure() == OutputStructure.NAMESPACES;
			if (doingNamespace) {
				writer.line();
				// top-level namespaces should not get 'export' sub-namespaces should. Vagaries of JavaScript
				writer.line("export namespace " + namespace.getName() + " {");
				writer.indentIn();
			}

			// sort namespaces for stability of output
			List<? extends JToplevelType> declarations = toList(namespace.getDeclarations());
			declarations.sort(Comparator.comparing(JToplevelType::getName));
			for (JType type : namespace.getDeclarations()) {
				type.accept(new TypeDeclarationProducer(writer, options));
			}
			outputEndpoints(namespace, writer);
			for (Namespace subNamespace : namespace.getNamespaces()) {
				outputNamespace(subNamespace);
			}
			if (doingNamespace) {
				writer.indentOut();
				writer.line("}");
			}
		} finally {
			outputController.close(writer);
		}
	}

	private List<JToplevelType> toList(Iterable<? extends JToplevelType> declarations) {
		List<JToplevelType> result = new ArrayList<>();
		for (JToplevelType i : declarations) {
			result.add(i);
		}
		return result;
	}

	private void start() throws IOException {
		TSFileWriter writer = outputController.getNamespaceHandler(TSFileWriter.JIG_NAMESPACE);
		try {
			boolean doingNamespace = options.getOutputStructure() == OutputStructure.NAMESPACES;
			if (doingNamespace) {
				writer.line("export namespace jsonInterfaceGenerator {");
				writer.indentIn();
			}
			Pattern pattern = Pattern.compile("^\\s*export type UnknownType = unknown");
			writer.writeResource("/header.ts", (line) -> {
				if (!options.isUseUnknown() && pattern.matcher(line).matches()) {
					return line.replace("unknown", "any");
				} else {
					return line;
				}
			});
			writer.line();
			if (doingNamespace) {
				writer.indentOut();
				writer.line("}");
			}
		} finally {
			outputController.close(writer);
		}

	}
}

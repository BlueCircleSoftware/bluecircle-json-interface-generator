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

package com.bluecirclesoft.open.jigen.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Holds information about a REST endpoint (one Java method may generate multiple endpoints, if it is annotated with multiple HTTP methods).
 */
public class Endpoint {

	private String id;

	private HttpMethod method;

	private JType responseBody;

	private String pathTemplate;

	private final List<EndpointParameter> parameters = new ArrayList<>();

	public Endpoint(String id) {
		this.id = id;
	}

	public Endpoint(String id, HttpMethod method, JObject responseBody) {
		this.id = id;
		this.method = method;
		this.responseBody = responseBody;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public List<EndpointParameter> getParameters() {
		return parameters;
	}

	public JType getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(JObject responseBody) {
		this.responseBody = responseBody;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setResponseBody(JType responseBody) {
		this.responseBody = responseBody;
	}

	public String getPathTemplate() {
		return pathTemplate;
	}

	public void setPathTemplate(String pathTemplate) {
		this.pathTemplate = pathTemplate;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("method", method)
				.append("parameters", parameters)
				.append("responseBody", responseBody)
				.append("pathTemplate", pathTemplate)
				.toString();
	}
}

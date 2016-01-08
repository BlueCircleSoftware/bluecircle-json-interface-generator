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

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO document me
 */
public class Endpoint {

	private final String id;

	private HttpMethod method;

	private JType requestBody;

	private JType responseBody;

	private String pathTemplate;

	private final Map<String, JType> pathParameters = new HashMap<>();

	private final Map<String, JType> requestParameters = new HashMap<>();

	public Endpoint(String id) {
		this.id = id;
	}

	public Endpoint(String id, HttpMethod method, JObject requestBody, JObject responseBody) {
		this.id = id;
		this.method = method;
		this.requestBody = requestBody;
		this.responseBody = responseBody;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public JType getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(JObject requestBody) {
		this.requestBody = requestBody;
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

	public void setRequestBody(JType requestBody) {
		this.requestBody = requestBody;
	}

	public void setResponseBody(JType responseBody) {
		this.responseBody = responseBody;
	}

	public Map<String, JType> getPathParameters() {
		return pathParameters;
	}

	public String getPathTemplate() {
		return pathTemplate;
	}

	public void setPathTemplate(String pathTemplate) {
		this.pathTemplate = pathTemplate;
	}

	public Map<String, JType> getRequestParameters() {
		return requestParameters;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("method", method)
				.append("requestBody", requestBody)
				.append("responseBody", responseBody)
				.append("pathTemplate", pathTemplate)
				.append("pathParameters", pathParameters)
				.append("requestParameters", requestParameters)
				.toString();
	}
}

package com.bluecirclesoft.open.jigen.model;

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
}

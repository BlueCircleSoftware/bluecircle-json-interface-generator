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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds information about a REST endpoint (one Java method may generate multiple endpoints, if it is annotated with multiple HTTP methods).
 */
@Getter
public class Endpoint implements Serializable {

	private final List<EndpointParameter> parameters = new ArrayList<>();

	@Setter
	private String consumes;

	@Setter
	private String produces;

	@Setter
	private String id;

	private HttpMethod method;

	@Setter
	private JType responseBody;

	@Setter
	private String pathTemplate;

	@Setter
	private Namespace namespace;

	Endpoint(String id) {
		this.id = id;
	}

	/**
	 * Are there any parameters of the specified type?
	 *
	 * @param sortedParams the parameters
	 * @param type         the desired type
	 * @return yes or no
	 */
	private static boolean hasType(Map<EndpointParameter.NetworkType, List<EndpointParameter>> sortedParams,
	                               EndpointParameter.NetworkType type) {
		List<EndpointParameter> endpointParameters = sortedParams.get(type);
		return endpointParameters != null && !endpointParameters.isEmpty();
	}

	public void setMethod(HttpMethod method) {
		assert method != null;
		this.method = method;
	}

	@Override
	public String toString() {
		return "Endpoint{" + "consumes='" + consumes + '\'' + ", produces='" + produces + '\'' + ", parameters=" + parameters + ", id='" +
				id + '\'' + ", method=" + method + ", responseBody=" + responseBody + ", pathTemplate='" + pathTemplate + '\'' +
				", namespace=" + namespace + '}';
	}

	/**
	 * Group parameters by their "network type"
	 *
	 * @return a map of lists of parameters, by network type.
	 */
	public Map<EndpointParameter.NetworkType, List<EndpointParameter>> getSortedParameters() {
		Map<EndpointParameter.NetworkType, List<EndpointParameter>> result = new HashMap<>();
		for (EndpointParameter parameter : parameters) {
			List<EndpointParameter> list = result.computeIfAbsent(parameter.getNetworkType(), k -> new ArrayList<>());
			list.add(parameter);
		}
		return result;
	}

	/**
	 * Is this endpoint a valid endpoint (i.e., can we generate a stub for it)?
	 *
	 * @return yes or no, along with an explanation if no
	 */
	public ValidEndpointResponse isValid() {
		Map<EndpointParameter.NetworkType, List<EndpointParameter>> sortedParams = getSortedParameters();
		List<EndpointParameter> bodyParams = sortedParams.get(EndpointParameter.NetworkType.JSON_BODY);
		boolean isBodyParam = bodyParams != null && !bodyParams.isEmpty();
		boolean isMultipleBodyParam = bodyParams != null && bodyParams.size() > 1;
		switch (method) {
			case POST:
				if (hasType(sortedParams, EndpointParameter.NetworkType.FORM) && isBodyParam) {
					return new ValidEndpointResponse(false,
							"Can't have both a form parameter parameter and body parameter on POST " + "requests");
				}
				if (isMultipleBodyParam) {
					return new ValidEndpointResponse(false, "Can't have multiple body parameters on POST requests");
				}
				break;
			case GET:
				if (isBodyParam) {
					return new ValidEndpointResponse(false, "Can't have a body parameter on GET requests");
				}
				if (hasType(sortedParams, EndpointParameter.NetworkType.FORM)) {
					return new ValidEndpointResponse(false, "Can't have a form parameter on GET requests");
				}
				break;
		}
		List<EndpointParameter> parameters1 = getParameters();
		for (int i = 0; i < parameters1.size(); i++) {
			EndpointParameter param = parameters1.get(i);
			if (param.getNetworkType() != EndpointParameter.NetworkType.JSON_BODY && StringUtils.isBlank(param.getNetworkName())) {
				return new ValidEndpointResponse(false, "No network name for parameter " + i);
			}
		}
		return new ValidEndpointResponse(true);
	}
}

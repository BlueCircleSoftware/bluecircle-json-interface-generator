/*
 * Copyright 2016 Blue Circle Software, LLC
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

/**
 * Holds relevant information about a web service method parameter.
 */
public class EndpointParameter {

	public enum NetworkType {
		// parameter to be placed in the URL path
		PATH,
		// parameter to be submitted in URL query section ('...?a=b&c=d...')
		QUERY,
		// parameter to be submitted over urlencoded POST
		FORM,
		// body of the submission of a POST
		BODY
	}

	private String codeName;

	private String networkName;

	private JType type;

	private NetworkType networkType;

	public EndpointParameter(String codeName, String networkName, JType type, NetworkType networkType) {
		this.codeName = codeName;
		this.networkName = networkName;
		this.type = type;
		this.networkType = networkType;
	}

	/**
	 * Get the name that should be used for this parameter in code (i.e., JavaScript/TypeScript/whatever parameter name)
	 *
	 * @return the code name
	 */
	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	/**
	 * Get the name for the parameter as it gets transmitted over the network (as a query parameter, form parameter, etc.)
	 *
	 * @return the network name
	 */
	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public void setType(JType type) {
		this.type = type;
	}

	/**
	 * Get the type of this parameter
	 *
	 * @return the type
	 */
	public JType getType() {
		return type;
	}

	/**
	 * Get the method that should be used to transmit this parameter over the network
	 *
	 * @return the network type
	 */
	public NetworkType getNetworkType() {
		return networkType;
	}

	public void setNetworkType(NetworkType networkType) {
		this.networkType = networkType;
	}
}

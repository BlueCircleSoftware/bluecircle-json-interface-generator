/*
 * Copyright 2024 Blue Circle Software, LLC
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
package com.bluecirclesoft.open.jigen.integrationJakartaee;

/**
 * Test json response
 */
public class JsonResponse {

	private String doubleA;

	private String doubleB;

	private String doubleBoth;


	public String getDoubleA() {
		return doubleA;
	}

	public void setDoubleA(String doubleA) {
		this.doubleA = doubleA;
	}

	public String getDoubleB() {
		return doubleB;
	}

	public void setDoubleB(String doubleB) {
		this.doubleB = doubleB;
	}

	public String getDoubleBoth() {
		return doubleBoth;
	}

	public void setDoubleBoth(String doubleBoth) {
		this.doubleBoth = doubleBoth;
	}

	@Override
	public String toString() {
		return "JsonResponse{" + "doubleA='" + doubleA + '\'' + ", doubleB='" + doubleB + '\'' + ", doubleBoth='" + doubleBoth + '\'' + '}';
	}
}

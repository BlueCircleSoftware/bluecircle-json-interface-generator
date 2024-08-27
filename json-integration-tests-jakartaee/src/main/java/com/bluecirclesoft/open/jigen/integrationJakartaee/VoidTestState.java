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

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import lombok.Getter;

/**
 * Test state for the {@link TestServicesVoid} test methods.  Basically, the test methods cat up a string, and at the end of the test, we
 * retrieve it and test it to make sure that all the methods were invoked properly.
 */
@Getter
@SessionScoped
public class VoidTestState implements Serializable {

	private String totalString = "";

	public void addToTotalString(String part) {
		this.totalString += part;
	}
}

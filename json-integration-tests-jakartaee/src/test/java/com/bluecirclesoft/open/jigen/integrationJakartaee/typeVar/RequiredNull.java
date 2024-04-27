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

package com.bluecirclesoft.open.jigen.integrationJakartaee.typeVar;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class that has a property marked "required", but when we try to create the new object JSON, the value is null (contradicting the
 * 'required', of course).
 */
public class RequiredNull {

	@JsonProperty(required = true)
	private String x;

	public String getX() {
		return x;
	}
}

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

package com.bluecirclesoft.open.jigen.spring;

import java.util.Set;

import org.springframework.http.MediaType;

import com.bluecirclesoft.open.jigen.model.HttpMethod;

/**
 * TODO document me
 */
class SpringRequestInfo {

	/**
	 * Mime type that this service consumes
	 */
	MediaType consumes;

	/**
	 * Mime type that this service produces
	 */
	MediaType produces;

	String path;

	Set<HttpMethod> methods;

	String validity;

	public SpringRequestInfo() {
	}

	@Override
	public String toString() {
		return "SpringRequestInfo{" + "consumes='" + consumes + '\'' + ", produces='" + produces + '\'' + ", path='" + path + '\'' +
				", methods=" + methods + ", validity='" + validity + '\'' + '}';
	}
}

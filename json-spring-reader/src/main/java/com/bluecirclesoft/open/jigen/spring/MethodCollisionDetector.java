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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bluecirclesoft.open.jigen.model.HttpMethod;

/**
 * TODO document me
 */
public class MethodCollisionDetector {

	private final Map<String, Map<HttpMethod, List<Method>>> map = new LinkedHashMap<>();

	public void addMethod(Method method, Iterable<HttpMethod> httpMethods) {
		String name = nameMethod(method);
		for (HttpMethod httpMethod : httpMethods) {
			map.computeIfAbsent(name, (n) -> new LinkedHashMap<>()).computeIfAbsent(httpMethod, (m) -> new ArrayList<>()).add(method);
		}
	}

	public void addMethod(Method method, HttpMethod httpMethod) {
		String name = nameMethod(method);
		map.computeIfAbsent(name, (n) -> new LinkedHashMap<>()).computeIfAbsent(httpMethod, (m) -> new ArrayList<>()).add(method);
	}

	public SuffixInfo getSuffixInfo(Method method, HttpMethod httpMethod) {
		String name = nameMethod(method);
		Map<HttpMethod, List<Method>> byMethod = map.get(name);
		if (byMethod == null) {
			return new SuffixInfo(false, null);
		}
		boolean multiHttpMethod = byMethod.size() > 1;
		List<Method> methods = byMethod.get(httpMethod);
		if (methods == null || methods.size() < 2) {
			return new SuffixInfo(multiHttpMethod, null);
		} else {
			return new SuffixInfo(multiHttpMethod, methods.indexOf(method));
		}
	}

	private static String nameMethod(Method method) {
		return method.getDeclaringClass().getName() + "." + method.getName();
	}
}

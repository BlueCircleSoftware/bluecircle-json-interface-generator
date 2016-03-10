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

package com.bluecirclesoft.open.jigen.jeeReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.helper.InsertingMap;
import com.bluecirclesoft.open.jigen.inputJackson.JacksonTypeModeller;
import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.HttpMethod;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
public class JavaEEModeller {

	private static final Logger logger = LoggerFactory.getLogger(JavaEEModeller.class);

	private static class MethodInfo {

		boolean consumer;

		boolean producer;

		Method method;

		public MethodInfo(Method method) {
			this.method = method;
		}
	}

	private static Map<Class<? extends Annotation>, HttpMethod> annotationHttpMethodMap = new HashMap<>();

	static {
		annotationHttpMethodMap.put(DELETE.class, HttpMethod.DELETE);
		annotationHttpMethodMap.put(GET.class, HttpMethod.GET);
		annotationHttpMethodMap.put(HEAD.class, HttpMethod.HEAD);
		annotationHttpMethodMap.put(POST.class, HttpMethod.POST);
		annotationHttpMethodMap.put(PUT.class, HttpMethod.PUT);
	}

	private Model model;

	private String urlPrefix;

	public Model createModel(String urlPrefix, String... packageNames) {
		InsertingMap<Method, MethodInfo> annotatedMethods = new InsertingMap<>(new HashMap<>(), MethodInfo::new);
		for (String packageName : packageNames) {
			Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packageName))
					.setScanners(new MethodAnnotationsScanner()));

			for (Method method : reflections.getMethodsAnnotatedWith(Produces.class)) {
				boolean producer = isProducer(method);
				if (producer) {
					annotatedMethods.elem(method).producer = true;
				}
			}

			for (Method method : reflections.getMethodsAnnotatedWith(Consumes.class)) {
				boolean consumer = isConsumer(method);
				if (consumer) {
					annotatedMethods.elem(method).consumer = true;
				}
			}
		}

		model = new Model();

		if (urlPrefix.endsWith("/")) {
			this.urlPrefix = urlPrefix;
		} else {
			this.urlPrefix = urlPrefix + "/";
		}

		for (MethodInfo method : annotatedMethods.values()) {
			readMethod(method);
		}

		return model;
	}

	private boolean isProducer(Method method) {
		Produces produces = method.getAnnotation(Produces.class);
		if (produces != null && produces.value() != null) {
			for (String val : produces.value()) {
				if (MediaType.APPLICATION_JSON.equals(val)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isConsumer(Method method) {
		Consumes consumes = method.getAnnotation(Consumes.class);
		if (consumes != null && consumes.value() != null) {
			for (String val : consumes.value()) {
				if (MediaType.APPLICATION_JSON.equals(val)) {
					return true;
				}
			}
		}
		return false;
	}

	public String joinPaths(String startElement, String... pathElements) {
		String result = startElement;
		for (String pathElement : pathElements) {
			if (pathElement != null) {
				if (result.endsWith("/")) {
					result = result.substring(0, result.length() - 1);
				}
				if (!pathElement.startsWith("/")) {
					result = result + "/";
				}
				result = result + pathElement;
			}
		}
		return result;
	}

	private void readMethod(MethodInfo methodInfo) {
		Method method = methodInfo.method;
		final Path methodPath = method.getAnnotation(Path.class);
		final Path classPath = method.getDeclaringClass().getAnnotation(Path.class);

		Set<HttpMethod> httpMethods = identifyHttpMethods(method);

		Map<String, MethodParameter> pathParameters = new LinkedHashMap<>();
		Map<String, MethodParameter> nonPathParameters = new LinkedHashMap<>();

		for (Parameter p : method.getParameters()) {
			MethodParameter mp = new MethodParameter();
			mp.setName(p.getName());
			mp.setType(p.getParameterizedType());
			if (p.isAnnotationPresent(PathParam.class)) {
				PathParam pathParam = p.getAnnotation(PathParam.class);
				mp.setName(pathParam.value());
				mp.setPathParam(true);
				pathParameters.put(mp.getName(), mp);
			} else {
				nonPathParameters.put(mp.getName(), mp);
			}
		}

		JType inType;
		JType outType;
		if (methodInfo.consumer) {
			if (nonPathParameters.size() > 1) {
				logger.warn("Cannot consume multiple JSON objects - not supported");
				return;
			}
			if (nonPathParameters.isEmpty()) {
				logger.warn("No parameter for request body - not supported");
			}
			JacksonTypeModeller modeller = new JacksonTypeModeller();
			inType = modeller.analyze(model, nonPathParameters.entrySet().iterator().next().getValue().getType());
		} else {
			inType = null;
		}

		if (methodInfo.producer) {
			JacksonTypeModeller modeller = new JacksonTypeModeller();
			outType = modeller.analyze(model, method.getGenericReturnType());
		} else {
			JacksonTypeModeller modeller = new JacksonTypeModeller();
			outType = modeller.analyze(model, String.class);
		}

		boolean appendHttpMethodName = httpMethods.size() > 1;

		for (HttpMethod httpMethod : httpMethods) {
			String suffix;
			if (appendHttpMethodName) {
				suffix = "_" + httpMethod.name();
			} else {
				suffix = "";
			}
			Endpoint endpoint = model.createEndpoint(method.getDeclaringClass().getName() + "." + method.getName() + suffix);
			endpoint.setRequestBody(inType);
			endpoint.setResponseBody(outType);
			endpoint.setPathTemplate(
					joinPaths(urlPrefix, classPath == null ? null : classPath.value(), methodPath == null ? null : methodPath.value()));
			for (MethodParameter pathParam : pathParameters.values()) {
				JacksonTypeModeller modeller = new JacksonTypeModeller();
				endpoint.getPathParameters().put(pathParam.getName(), modeller.analyze(model, pathParam.getType()));
			}
			endpoint.setMethod(httpMethod);
		}
	}

	private Set<HttpMethod> identifyHttpMethods(Method method) {
		Set<HttpMethod> result = EnumSet.noneOf(HttpMethod.class);
		for (Map.Entry<Class<? extends Annotation>, HttpMethod> entry : annotationHttpMethodMap.entrySet()) {
			if (method.isAnnotationPresent(entry.getKey())) {
				result.add(entry.getValue());
			}
		}
		return result;
	}

	public Model getModel() {
		return model;
	}
}

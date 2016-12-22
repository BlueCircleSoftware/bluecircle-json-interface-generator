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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
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
import com.bluecirclesoft.open.jigen.model.EndpointParameter;
import com.bluecirclesoft.open.jigen.model.HttpMethod;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
class JavaEEModeller {

	private static final Logger logger = LoggerFactory.getLogger(JavaEEModeller.class);

	private static class MethodInfo {

		boolean consumer;

		boolean producer;

		Method method;

		MethodInfo(Method method) {
			this.method = method;
		}
	}

	private static final Map<Class<? extends Annotation>, HttpMethod> annotationHttpMethodMap = new HashMap<>();

	static {
		annotationHttpMethodMap.put(DELETE.class, HttpMethod.DELETE);
		annotationHttpMethodMap.put(GET.class, HttpMethod.GET);
		annotationHttpMethodMap.put(HEAD.class, HttpMethod.HEAD);
		annotationHttpMethodMap.put(POST.class, HttpMethod.POST);
		annotationHttpMethodMap.put(PUT.class, HttpMethod.PUT);
	}

	private Model model;

	private String urlPrefix;

	Model createModel(String urlPrefix, String... packageNames) {
		InsertingMap<Method, MethodInfo> annotatedMethods = new InsertingMap<>(new HashMap<>(), MethodInfo::new);
		for (String packageName : packageNames) {
			Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packageName))
					.setScanners(new MethodAnnotationsScanner()));

			for (Method method : findJaxRsMethods(reflections)) {
				logger.info("Handling method {}", method);
				boolean producer = isProducer(method);
				if (producer) {
					annotatedMethods.elem(method).producer = true;
				}
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

	/**
	 * Find methods which are JAX-RS methods
	 *
	 * @param reflections the reflections object
	 * @return a set of all appropriate methods
	 */
	private static Set<Method> findJaxRsMethods(Reflections reflections) {
		Set<Method> resultSet = new TreeSet<>((Method m1, Method m2) -> {
			int result = m1.getDeclaringClass().getName().compareTo(m2.getDeclaringClass().getName());
			if (result != 0) {
				return result;
			}
			return m1.getName().compareTo(m2.getName());
		});
		for (Class<? extends Annotation> annotation : annotationHttpMethodMap.keySet()) {
			resultSet.addAll(reflections.getMethodsAnnotatedWith(annotation));
		}
		return resultSet;
	}

	private static boolean isProducer(Method method) {
		Produces produces = method.getAnnotation(Produces.class);
		if (isJsonProducer(produces)) {
			return true;
		} else {
			produces = method.getDeclaringClass().getAnnotation(Produces.class);
			return isJsonProducer(produces);
		}
	}

	private static boolean isJsonProducer(Produces produces) {
		if (produces != null) {
			for (String val : produces.value()) {
				if (MediaType.APPLICATION_JSON.equals(val)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isConsumer(Method method) {
		Consumes consumes = method.getAnnotation(Consumes.class);
		if (isJsonConsumer(consumes)) {
			return true;
		} else {
			consumes = method.getDeclaringClass().getAnnotation(Consumes.class);
			return isJsonConsumer(consumes);
		}
	}

	private static boolean isJsonConsumer(Consumes consumes) {
		if (consumes != null) {
			for (String val : consumes.value()) {
				if (MediaType.APPLICATION_JSON.equals(val)) {
					return true;
				}
			}
		}
		return false;
	}

	private static String joinPaths(String startElement, String... pathElements) {
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

		List<MethodParameter> parameters = new ArrayList<>();
		MethodParameter bodyParam = null;

		for (Parameter p : method.getParameters()) {
			MethodParameter mp = new MethodParameter();
			mp.setCodeName(p.getName());
			mp.setType(p.getParameterizedType());
			if (p.isAnnotationPresent(PathParam.class)) {
				PathParam pathParam = p.getAnnotation(PathParam.class);
				mp.setNetworkName(pathParam.value());
				mp.setNetworkType(EndpointParameter.NetworkType.PATH);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(QueryParam.class)) {
				QueryParam queryParam = p.getAnnotation(QueryParam.class);
				mp.setNetworkName(queryParam.value());
				mp.setNetworkType(EndpointParameter.NetworkType.QUERY);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(FormParam.class)) {
				FormParam formParam = p.getAnnotation(FormParam.class);
				mp.setNetworkName(formParam.value());
				mp.setNetworkType(EndpointParameter.NetworkType.FORM);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(MatrixParam.class) || p.isAnnotationPresent(HeaderParam.class) ||
					p.isAnnotationPresent(CookieParam.class) || p.isAnnotationPresent(Context.class)) {
				// ignore
			} else {
				mp.setNetworkType(EndpointParameter.NetworkType.BODY);
				parameters.add(mp);
				bodyParam = mp;
			}
		}

		JType outType;
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
			endpoint.setResponseBody(outType);
			endpoint.setPathTemplate(
					joinPaths(classPath == null ? null : classPath.value(), methodPath == null ? null : methodPath.value()));
			for (MethodParameter pathParam : parameters) {
				JacksonTypeModeller modeller = new JacksonTypeModeller();
				endpoint.getParameters()
						.add(new EndpointParameter(pathParam.getCodeName(), pathParam.getNetworkName(),
								modeller.analyze(model, pathParam.getType()), pathParam.getNetworkType()));
			}
			endpoint.setMethod(httpMethod);
		}
	}

	private static Set<HttpMethod> identifyHttpMethods(Method method) {
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

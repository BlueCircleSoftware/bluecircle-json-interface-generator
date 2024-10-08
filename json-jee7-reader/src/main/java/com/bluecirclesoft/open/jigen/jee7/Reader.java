/*
 * Copyright 2019 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.jee7;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.BeanParam;
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

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.ClassOverrideHandler;
import com.bluecirclesoft.open.jigen.ModelCreator;
import com.bluecirclesoft.open.jigen.annotations.Generate;
import com.bluecirclesoft.open.jigen.jacksonModeller.IncludeSubclasses;
import com.bluecirclesoft.open.jigen.jacksonModeller.JacksonTypeModeller;
import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.EndpointParameter;
import com.bluecirclesoft.open.jigen.model.HttpMethod;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.model.SourcedType;
import com.bluecirclesoft.open.jigen.model.ValidEndpointResponse;

/**
 * Read Java packages and look for JAX-RS methods - convert those into a {@link Model}
 */
public class Reader implements ModelCreator<Options> {

	private static final Logger logger = LoggerFactory.getLogger(Reader.class);

	private static final Map<Class<? extends Annotation>, HttpMethod> annotationHttpMethodMap = new HashMap<>();

	private static class MethodInfo {

		String consumes;

		String produces;

		final Method method;

		MethodInfo(Method method) {
			this.method = method;
		}
	}

	static {
		annotationHttpMethodMap.put(DELETE.class, HttpMethod.DELETE);
		annotationHttpMethodMap.put(GET.class, HttpMethod.GET);
		annotationHttpMethodMap.put(HEAD.class, HttpMethod.HEAD);
		annotationHttpMethodMap.put(POST.class, HttpMethod.POST);
		annotationHttpMethodMap.put(PUT.class, HttpMethod.PUT);
	}

	private final ClassOverrideHandler classOverrideHandler = new ClassOverrideHandler();

	private Options options;

	private Model model;

	private JEnum.EnumType defaultEnumType = JEnum.EnumType.NUMERIC;

	private JacksonTypeModeller modeller;

	/**
	 * Find methods which are JAX-RS methods
	 *
	 * @param reflections the reflections object
	 * @return a set of all appropriate methods
	 */
	private static Set<Method> findJaxRsMethods(Reflections reflections) {
		Set<Method> resultSet =
				new TreeSet<>(Comparator.comparing((Method m) -> m.getDeclaringClass().getName()).thenComparing(Method::getName));
		for (Class<? extends Annotation> annotation : annotationHttpMethodMap.keySet()) {
			resultSet.addAll(reflections.getMethodsAnnotatedWith(annotation));
		}
		return resultSet;
	}

	/**
	 * Find classes which are tagged with {@link Generate}
	 *
	 * @param reflections the reflections object
	 * @return a set of all appropriate classes
	 */
	private static Set<Class<?>> findClassesTaggedGenerate(Reflections reflections) {
		return reflections.getTypesAnnotatedWith(Generate.class);
	}

	private static boolean isProducer(Method method) {
		Produces produces = method.getAnnotation(Produces.class);
		if (isJsonProducer(produces)) {
			return true;
		} else if (method.getGenericReturnType() == Void.TYPE) {
			return true;
		} else {
			produces = method.getDeclaringClass().getAnnotation(Produces.class);
			return isJsonProducer(produces);
		}
	}

	/**
	 * Does this method return JSON and only JSON?
	 *
	 * @param produces
	 * @return
	 */
	private static boolean isJsonProducer(Produces produces) {
		if (produces != null) {
			for (String val : produces.value()) {
				for (String elem : val.split(",")) {
					if (!MediaType.APPLICATION_JSON.equals(elem.trim())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static String isConsumer(Method method) {
		Consumes consumes = method.getAnnotation(Consumes.class);
		String found = getConsumerString(consumes);
		if (found != null) {
			return found;
		} else {
			consumes = method.getDeclaringClass().getAnnotation(Consumes.class);
			return getConsumerString(consumes);
		}
	}

	private static String getConsumerString(Consumes consumes) {
		String found = null;
		if (consumes != null) {
			for (String val : consumes.value()) {
				for (String elem : val.split(",")) {
					String trimmedElem = elem.trim();
					if (MediaType.APPLICATION_JSON.equals(trimmedElem)) {
						if (found == null || found.equals(MediaType.APPLICATION_JSON)) {
							found = MediaType.APPLICATION_JSON;
						} else {
							return null;
						}
					} else if (MediaType.APPLICATION_FORM_URLENCODED.equals(trimmedElem)) {
						if (found == null || found.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
							found = MediaType.APPLICATION_FORM_URLENCODED;
						} else {
							return null;
						}
					} else {
						return null;
					}
				}
			}
		}
		return found;
	}

	private static String joinPaths(String... pathElements) {
		StringBuilder pathBuilder = new StringBuilder();
		for (String pathElement : pathElements) {
			if (pathElement != null) {
				if (pathBuilder.length() > 0 && pathBuilder.charAt(pathBuilder.length() - 1) == '/') {
					pathBuilder.deleteCharAt(pathBuilder.length() - 1);
				}
				if (!pathElement.startsWith("/")) {
					pathBuilder.append("/");
				}
				pathBuilder.append(pathElement);
			}
		}
		String result = pathBuilder.toString();
		if (StringUtils.isBlank(result)) {
			throw new RuntimeException("No path provided (on class or method)");
		}
		return result;
	}

	private static boolean isValidJavaIdentifier(CharSequence value) {
		for (int i = 0; i < value.length(); i++) {
			if (i == 0) {
				if (!Character.isJavaIdentifierStart(value.charAt(i))) {
					return false;
				}
			} else {
				if (!Character.isJavaIdentifierPart(value.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}

	private static Set<HttpMethod> identifyHttpMethods(AnnotatedElement method) {
		Set<HttpMethod> result = EnumSet.noneOf(HttpMethod.class);
		for (Map.Entry<Class<? extends Annotation>, HttpMethod> entry : annotationHttpMethodMap.entrySet()) {
			if (method.isAnnotationPresent(entry.getKey())) {
				result.add(entry.getValue());
			}
		}
		return result;
	}

	private void createModel(String... packageNames) {
		Map<Method, MethodInfo> annotatedMethods = new HashMap<>();
		for (String packageName : packageNames) {
			logger.info("Reading package {}", packageName);
			Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packageName))
					.setScanners(Scanners.MethodsAnnotated, Scanners.TypesAnnotated, Scanners.SubTypes));

			for (Method method : findJaxRsMethods(reflections)) {
				logger.info("Reading method {}", method);
				boolean producer = isProducer(method);
				if (producer) {
					annotatedMethods.computeIfAbsent(method, MethodInfo::new).produces = MediaType.APPLICATION_JSON;
				}
				String consumes = isConsumer(method);
				if (consumes != null) {
					annotatedMethods.computeIfAbsent(method, MethodInfo::new).consumes = consumes;
				}
			}

			SourcedType generatedSource = new SourcedType(null, "@Generate annotation search", null);
			for (Class<?> generatedClass : findClassesTaggedGenerate(reflections)) {
				modeller.readOneType(model, new SourcedType(generatedClass, String.valueOf(generatedClass), generatedSource));
			}
		}

		for (MethodInfo method : annotatedMethods.values()) {
			try {
				readMethod(method);
			} catch (Exception e) {
				throw new RuntimeException("Error processing JAX-RS method " + method.method, e);
			}
		}

	}

	private void readMethod(MethodInfo methodInfo) {
		Method method = methodInfo.method;

		SourcedType methodSource = new SourcedType(null, "Method " + method, null);

		final Path methodPath = method.getAnnotation(Path.class);
		final Path classPath = method.getDeclaringClass().getAnnotation(Path.class);

		Set<HttpMethod> httpMethods = identifyHttpMethods(method);

		Collection<MethodParameter> parameters = new ArrayList<>();

		for (Parameter p : method.getParameters()) {
			MethodParameter mp = new MethodParameter();
			boolean hasName = p.isNamePresent();
			mp.setCodeName(p.getName());
			mp.setType(p.getParameterizedType());
			if (p.isAnnotationPresent(PathParam.class)) {
				PathParam pathParam = p.getAnnotation(PathParam.class);
				if (!hasName && isValidJavaIdentifier(pathParam.value())) {
					mp.setCodeName(pathParam.value());
				}
				mp.setNetworkName(pathParam.value());
				mp.setNetworkType(EndpointParameter.NetworkType.PATH);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(QueryParam.class)) {
				QueryParam queryParam = p.getAnnotation(QueryParam.class);
				if (!hasName && isValidJavaIdentifier(queryParam.value())) {
					mp.setCodeName(queryParam.value());
				}
				mp.setNetworkName(queryParam.value());
				mp.setNetworkType(EndpointParameter.NetworkType.QUERY);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(FormParam.class)) {
				FormParam formParam = p.getAnnotation(FormParam.class);
				if (!hasName && isValidJavaIdentifier(formParam.value())) {
					mp.setCodeName(formParam.value());
				}
				mp.setNetworkName(formParam.value());
				mp.setNetworkType(EndpointParameter.NetworkType.FORM);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(MatrixParam.class) || p.isAnnotationPresent(HeaderParam.class) ||
					p.isAnnotationPresent(CookieParam.class) || p.isAnnotationPresent(Context.class)) {
				// ignore
			} else if (p.isAnnotationPresent(BeanParam.class)) {
				logger.warn("Cannot handle @BeanParam parameters - skipping method");
				return;
			} else {
				mp.setNetworkType(EndpointParameter.NetworkType.JSON_BODY);
				parameters.add(mp);
			}
		}

		JType outType;
		if (methodInfo.produces != null) {
			outType = modeller.readOneType(model,
					new SourcedType(method.getGenericReturnType(), "Return type of method " + methodInfo.method, methodSource));
		} else {
			outType =
					modeller.readOneType(model, new SourcedType(String.class, "Return type of method " + methodInfo.method, methodSource));
		}

		boolean appendHttpMethodName = httpMethods.size() > 1;

		for (HttpMethod httpMethod : httpMethods) {
			String suffix;
			if (appendHttpMethodName) {
				suffix = "_" + httpMethod.name();
			} else {
				suffix = "";
			}
			String endpointName = method.getDeclaringClass().getName() + "." + method.getName() + suffix;

			Endpoint endpoint = model.createEndpoint(endpointName);
			endpoint.setResponseBody(outType);
			endpoint.setPathTemplate(options.getUrlPrefix() +
					joinPaths(classPath == null ? null : classPath.value(), methodPath == null ? null : methodPath.value()));
			for (MethodParameter methodParameter : parameters) {
				endpoint.getParameters()
						.add(new EndpointParameter(methodParameter.getCodeName(), methodParameter.getNetworkName(),
								modeller.readOneType(model,
										new SourcedType(methodParameter.getType(), "Parameter " + methodParameter.getCodeName(),
												methodSource)), methodParameter.getNetworkType()));
			}
			endpoint.setMethod(httpMethod);
			endpoint.setConsumes(methodInfo.consumes);
			endpoint.setProduces(methodInfo.produces);

			// check validity
			ValidEndpointResponse validity = endpoint.isValid();
			if (!validity.ok) {
				logger.warn("Problems encountered while reading method {}:", method);
				for (String problem : validity.problems) {
					logger.warn("error: {}", problem);
				}
				model.removeEndpoint(endpoint);
			} else {
				logger.info("Added endpoint {} at {} method {}", endpointName, endpoint.getPathTemplate(), endpoint.getMethod());
			}
		}
	}

	@Override
	public Class<Options> getOptionsClass() {
		return Options.class;
	}

	@Override
	public void model(Model model) {
		try {
			this.model = model;
			defaultEnumType = options.isDefaultStringEnums() ? JEnum.EnumType.STRING : JEnum.EnumType.NUMERIC;
			String[] packArr = options.getPackages().toArray(new String[0]);
			classOverrideHandler.ingestOverrides(options.getClassSubstitutions());
			this.modeller = new JacksonTypeModeller(classOverrideHandler, defaultEnumType,
					options.isIncludeSubclasses() ? IncludeSubclasses.INCLUDE : IncludeSubclasses.EXCLUDE, packArr);
			createModel(packArr);
		} catch (Throwable t) {
			logger.error("Caught exception creating model: ", t);
			throw t;
		}
	}


	@Override
	public void acceptOptions(Object options, List<? super String> errors) {
		this.options = (Options) options;
		if (this.options.getPackages() == null || this.options.getPackages().isEmpty()) {
			errors.add("Package name to process is required.");
		}
	}
}

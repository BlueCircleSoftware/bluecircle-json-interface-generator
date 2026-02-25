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

package com.bluecirclesoft.open.jigen.jakartaee;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

/**
 * Read Java packages and look for JAX-RS methods - convert those into a {@link Model}
 */
public class Reader implements ModelCreator<Options> {

	private static final Logger logger = LoggerFactory.getLogger(Reader.class);

	private static final Map<Class<? extends Annotation>, HttpMethod> annotationHttpMethodMap = new HashMap<>();
	private static final Comparator<Method> METHOD_COMPARATOR = Comparator
			.comparing((Method m) -> m.getDeclaringClass().getName())
			.thenComparing(Method::getName)
			.thenComparing(Reader::methodSignature);

	private static class MethodInfo {

		String consumes;

		String produces;

		boolean overloadedName;

		String parameterSignature;

		final Method method;

		MethodInfo(Method method) {
			this.method = method;
		}
	}

	static {
		annotationHttpMethodMap.put(DELETE.class, HttpMethod.DELETE);
		annotationHttpMethodMap.put(GET.class, HttpMethod.GET);
		annotationHttpMethodMap.put(HEAD.class, HttpMethod.HEAD);
		annotationHttpMethodMap.put(OPTIONS.class, HttpMethod.OPTIONS);
		annotationHttpMethodMap.put(POST.class, HttpMethod.POST);
		annotationHttpMethodMap.put(PUT.class, HttpMethod.PUT);
		addHttpMethodAnnotation("jakarta.ws.rs.PATCH", HttpMethod.PATCH);
	}

	private static void addHttpMethodAnnotation(String className, HttpMethod method) {
		try {
			Class<?> annotationClass = Class.forName(className);
			if (Annotation.class.isAssignableFrom(annotationClass)) {
				@SuppressWarnings("unchecked")
				Class<? extends Annotation> typedAnnotation = (Class<? extends Annotation>) annotationClass;
				annotationHttpMethodMap.put(typedAnnotation, method);
			}
		} catch (ClassNotFoundException ignored) {
			// optional dependency
		}
	}

	private static String methodSignature(Method method) {
		return method.toGenericString();
	}

	private static String buildParameterSignature(Method method) {
		Type[] types = method.getGenericParameterTypes();
		if (types.length == 0) {
			return "noArgs";
		}
		StringBuilder signature = new StringBuilder();
		for (Type type : types) {
			if (signature.length() > 0) {
				signature.append('_');
			}
			signature.append(sanitizeTypeName(type.getTypeName()));
		}
		return signature.toString();
	}

	private static String sanitizeTypeName(String typeName) {
		StringBuilder cleaned = new StringBuilder(typeName.length());
		for (int i = 0; i < typeName.length(); i++) {
			char ch = typeName.charAt(i);
			if (Character.isLetterOrDigit(ch)) {
				cleaned.append(ch);
			} else {
				cleaned.append('_');
			}
		}
		return cleaned.toString();
	}

	private static List<String> splitMediaTypes(String[] values) {
		List<String> result = new ArrayList<>();
		for (String value : values) {
			for (String elem : value.split(",")) {
				String trimmed = elem.trim();
				if (!trimmed.isEmpty()) {
					result.add(trimmed);
				}
			}
		}
		return result;
	}

	private static String chooseJsonMediaType(List<String> mediaTypes) {
		String applicationJsonValue = null;
		String fallback = null;
		for (String mediaType : mediaTypes) {
			String base = stripParameters(mediaType);
			if (MediaType.APPLICATION_JSON.equalsIgnoreCase(base)) {
				if (applicationJsonValue == null || MediaType.APPLICATION_JSON.equalsIgnoreCase(mediaType)) {
					applicationJsonValue = mediaType;
				}
			} else if (fallback == null) {
				fallback = mediaType;
			}
		}
		return applicationJsonValue != null ? applicationJsonValue : fallback;
	}

	private static boolean isJsonLike(String mediaType) {
		String base = stripParameters(mediaType).toLowerCase(Locale.ROOT);
		if (MediaType.APPLICATION_JSON.equals(base)) {
			return true;
		}
		int slash = base.indexOf('/');
		if (slash == -1) {
			return false;
		}
		String type = base.substring(0, slash);
		String subtype = base.substring(slash + 1);
		return "application".equals(type) && (subtype.equals("json") || subtype.endsWith("+json"));
	}

	private static boolean isFormUrlEncoded(String mediaType) {
		String base = stripParameters(mediaType);
		return MediaType.APPLICATION_FORM_URLENCODED.equalsIgnoreCase(base);
	}

	private static String stripParameters(String mediaType) {
		int semicolonIndex = mediaType.indexOf(';');
		if (semicolonIndex == -1) {
			return mediaType.trim();
		}
		return mediaType.substring(0, semicolonIndex).trim();
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
		Set<Method> resultSet = new TreeSet<>(METHOD_COMPARATOR);
		for (Class<? extends Annotation> annotation : annotationHttpMethodMap.keySet()) {
			resultSet.addAll(reflections.getMethodsAnnotatedWith(annotation));
		}
		for (Class<?> annotationType : reflections.getTypesAnnotatedWith(jakarta.ws.rs.HttpMethod.class)) {
			if (Annotation.class.isAssignableFrom(annotationType)) {
				@SuppressWarnings("unchecked")
				Class<? extends Annotation> typedAnnotation = (Class<? extends Annotation>) annotationType;
				resultSet.addAll(reflections.getMethodsAnnotatedWith(typedAnnotation));
			}
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

	private static String getProducerString(Method method) {
		Produces produces = method.getAnnotation(Produces.class);
		if (produces != null) {
			return getProducerString(produces);
		}
		produces = method.getDeclaringClass().getAnnotation(Produces.class);
		return getProducerString(produces);
	}

	/**
	 * Does this method return JSON and only JSON?
	 *
	 * @param produces
	 * @return a JSON media type string if appropriate, otherwise null
	 */
	private static String getProducerString(Produces produces) {
		if (produces == null) {
			return null;
		}
		List<String> mediaTypes = splitMediaTypes(produces.value());
		if (mediaTypes.isEmpty()) {
			return null;
		}
		for (String mediaType : mediaTypes) {
			if (!isJsonLike(mediaType)) {
				return null;
			}
		}
		return chooseJsonMediaType(mediaTypes);
	}

	private static String isConsumer(Method method) {
		Consumes consumes = method.getAnnotation(Consumes.class);
		if (consumes != null) {
			return getConsumerString(consumes);
		}
		consumes = method.getDeclaringClass().getAnnotation(Consumes.class);
		return getConsumerString(consumes);
	}

	private static String getConsumerString(Consumes consumes) {
		if (consumes == null) {
			return null;
		}
		List<String> mediaTypes = splitMediaTypes(consumes.value());
		if (mediaTypes.isEmpty()) {
			return null;
		}
		boolean hasForm = false;
		List<String> jsonTypes = new ArrayList<>();
		for (String mediaType : mediaTypes) {
			if (isFormUrlEncoded(mediaType)) {
				hasForm = true;
			} else if (isJsonLike(mediaType)) {
				jsonTypes.add(mediaType);
			} else {
				return null;
			}
		}
		if (hasForm && !jsonTypes.isEmpty()) {
			return null;
		}
		if (hasForm) {
			return MediaType.APPLICATION_FORM_URLENCODED;
		}
		if (jsonTypes.isEmpty()) {
			return null;
		}
		return chooseJsonMediaType(jsonTypes);
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
		for (Annotation annotation : method.getAnnotations()) {
			jakarta.ws.rs.HttpMethod httpMethod = annotation.annotationType().getAnnotation(jakarta.ws.rs.HttpMethod.class);
			if (httpMethod != null) {
				String methodName = httpMethod.value().toUpperCase(Locale.ROOT);
				try {
					result.add(HttpMethod.valueOf(methodName));
				} catch (IllegalArgumentException e) {
					logger.warn("Unknown HTTP method annotation value {}", methodName);
				}
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
			String produces = getProducerString(method);
			if (produces != null) {
				annotatedMethods.computeIfAbsent(method, MethodInfo::new).produces = produces;
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

		Map<String, Integer> methodNameCounts = new HashMap<>();
		for (Method method : annotatedMethods.keySet()) {
			String key = method.getDeclaringClass().getName() + "#" + method.getName();
			methodNameCounts.put(key, methodNameCounts.getOrDefault(key, 0) + 1);
		}
		for (MethodInfo method : annotatedMethods.values()) {
			String key = method.method.getDeclaringClass().getName() + "#" + method.method.getName();
			method.overloadedName = methodNameCounts.getOrDefault(key, 0) > 1;
			if (method.overloadedName) {
				method.parameterSignature = buildParameterSignature(method.method);
			}
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
			outType = modeller.readOneType(model, new SourcedType(method.getGenericReturnType(), "Return type", methodSource));
		} else {
			outType = modeller.readOneType(model, new SourcedType(String.class, "Return type", methodSource));
		}

		boolean appendHttpMethodName = httpMethods.size() > 1;

		for (HttpMethod httpMethod : httpMethods) {
			String suffix;
			if (appendHttpMethodName) {
				suffix = "_" + httpMethod.name();
			} else {
				suffix = "";
			}
			String endpointName = method.getDeclaringClass().getName() + "." + method.getName();
			if (methodInfo.overloadedName && methodInfo.parameterSignature != null) {
				endpointName = endpointName + "__" + methodInfo.parameterSignature;
			}
			endpointName = endpointName + suffix;

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

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

package com.bluecirclesoft.open.jigen.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bluecirclesoft.open.jigen.ClassOverrideHandler;
import com.bluecirclesoft.open.jigen.ModelCreator;
import com.bluecirclesoft.open.jigen.annotations.Generate;
import com.bluecirclesoft.open.jigen.jacksonModeller.IncludeSubclasses;
import com.bluecirclesoft.open.jigen.jacksonModeller.JacksonTypeModeller;
import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.EndpointParameter;
import com.bluecirclesoft.open.jigen.model.HttpMethod;
import com.bluecirclesoft.open.jigen.model.JAny;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.model.PropertyEnumerator;
import com.bluecirclesoft.open.jigen.model.SourcedType;
import com.bluecirclesoft.open.jigen.model.ValidEndpointResponse;
import com.bluecirclesoft.open.jigen.spring.springmodel.AnnotationInstance;
import com.bluecirclesoft.open.jigen.spring.springmodel.GlobalAnnotationMap;

/**
 * TODO document me
 */
public class Reader implements ModelCreator<Options> {

	private static final Logger logger = LoggerFactory.getLogger(Reader.class);

	private static final Map<RequestMethod, HttpMethod> httpMethodMap = new EnumMap<>(RequestMethod.class);

	private static final Collection<HttpMethod> jsonnableMethods = EnumSet.noneOf(HttpMethod.class);

	private static class MethodInfo {

		boolean consumer;

		boolean producer;

		final Method method;

		final SpringRequestInfo springRequestInfo;

		MethodInfo(Method method, SpringRequestInfo springRequestInfo) {
			this.method = method;
			this.springRequestInfo = springRequestInfo;
		}
	}

	static {
		httpMethodMap.put(RequestMethod.DELETE, HttpMethod.DELETE);
		httpMethodMap.put(RequestMethod.GET, HttpMethod.GET);
		httpMethodMap.put(RequestMethod.HEAD, HttpMethod.HEAD);
		httpMethodMap.put(RequestMethod.POST, HttpMethod.POST);
		httpMethodMap.put(RequestMethod.PUT, HttpMethod.PUT);
		httpMethodMap.put(RequestMethod.OPTIONS, HttpMethod.OPTIONS);
		httpMethodMap.put(RequestMethod.PATCH, HttpMethod.PATCH);
		httpMethodMap.put(RequestMethod.TRACE, HttpMethod.TRACE);

		jsonnableMethods.add(HttpMethod.GET);
		jsonnableMethods.add(HttpMethod.POST);
		jsonnableMethods.add(HttpMethod.PUT);
		jsonnableMethods.add(HttpMethod.PATCH);
		jsonnableMethods.add(HttpMethod.DELETE);
	}

	private final GlobalAnnotationMap annotationMap = new GlobalAnnotationMap();

	private Options options;

	private final ClassOverrideHandler classOverrideHandler = new ClassOverrideHandler();

	private JEnum.EnumType defaultEnumType = JEnum.EnumType.NUMERIC;

	private Model model;

	private String[] packageNames;

	private static boolean isProducer(Method method, SpringRequestInfo springRequestInfo) {
		return springRequestInfo.produces == MediaType.APPLICATION_JSON || method.getGenericReturnType() == Void.TYPE;
	}

	private static boolean isConsumer(Method method, SpringRequestInfo springRequestInfo) {
		return springRequestInfo.consumes == MediaType.APPLICATION_JSON ||
				springRequestInfo.consumes == MediaType.APPLICATION_FORM_URLENCODED;
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

	private static String prependSlash(String c) {
		if (c != null && c.charAt(0) != '/') {
			return '/' + c;
		} else {
			return c;
		}
	}

	private static boolean isValidJavaIdentifier(String valueIn) {
		if (valueIn == null) {
			// noll is not a valid identifier
			return false;
		}
		String value = valueIn.trim();
		if (value.isEmpty()) {
			// the empty string is not a valid identifier
			return false;
		}
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

	/**
	 * Find classes which are tagged with {@link Generate}
	 *
	 * @param reflections the reflections object
	 * @return a set of all appropriate classes
	 */
	private static Set<Class<?>> findClassesTaggedGenerate(Reflections reflections) {
		return reflections.getTypesAnnotatedWith(Generate.class);
	}

	private void createModel(String... packageNames) {

		assert packageNames != null;
		assert packageNames.length > 0;

		annotationMap.ingestAnnotations(packageNames);

		Map<Method, MethodInfo> annotatedMethods = new HashMap<>();
		for (String packageName : packageNames) {
			Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packageName))
					.setScanners(Scanners.MethodsAnnotated, Scanners.TypesAnnotated, Scanners.SubTypes));

			Set<Method> allMethods = findRequestMappingMethods(reflections);
			for (Method method : allMethods) {
				SpringRequestInfo springInfo = getMethodInfo(method);
				if (springInfo.methods == null || springInfo.methods.isEmpty()) {
					// method had no interesting HTTP methods
					continue;
				}
				logger.debug("Handling method {}, Spring info is {}", method, springInfo);
				if (springInfo.validity != null) {
					logger.warn("Problems encountered while reading method {}:", method);
					logger.warn("error: {}", springInfo.validity);
					continue;
				}
				boolean producer = isProducer(method, springInfo);
				logger.debug("Method {} - isProducer? {}", method, producer);
				boolean consumer = isConsumer(method, springInfo);
				logger.debug("Method {} - isConsumer? {}", method, consumer);
				if (producer || consumer) {
					MethodInfo newMethodInfo = annotatedMethods.computeIfAbsent(method, (m) -> new MethodInfo(m, springInfo));
					newMethodInfo.producer = producer;
					newMethodInfo.consumer = consumer;
				}
			}

			SourcedType generatedSource = new SourcedType(null, "@Generate annotation search", null);
			for (Class<?> generatedClass : findClassesTaggedGenerate(reflections)) {
				PropertyEnumerator modeller = new JacksonTypeModeller(classOverrideHandler, defaultEnumType,
						options.isIncludeSubclasses() ? IncludeSubclasses.INCLUDE : IncludeSubclasses.EXCLUDE, packageNames);
				modeller.readOneType(model, new SourcedType(generatedClass, String.valueOf(generatedClass), generatedSource));
			}
		}

		MethodCollisionDetector detector = new MethodCollisionDetector();
		for (MethodInfo method : annotatedMethods.values()) {
			detector.addMethod(method.method, method.springRequestInfo.methods);
		}

		for (MethodInfo method : annotatedMethods.values()) {
			try {
				readMethod(method, detector);
			} catch (Exception e) {
				throw new RuntimeException("Error processing Spring request method " + method.method, e);
			}
		}

	}

	/**
	 * Find methods which are RequestMapping methods
	 *
	 * @param reflections the reflections object
	 * @return a set of all appropriate methods
	 */
	private Set<Method> findRequestMappingMethods(Reflections reflections) {
		Set<Method> resultSet =
				new TreeSet<>(Comparator.comparing((Method m) -> m.getDeclaringClass().getName()).thenComparing(Method::getName));
		for (Class<? extends Annotation> annotation : this.annotationMap.getAllAnnotations()) {
			resultSet.addAll(reflections.getMethodsAnnotatedWith(annotation));
		}
		return resultSet;
	}

	private void readMethod(MethodInfo methodInfo, MethodCollisionDetector detector) {
		Method method = methodInfo.method;
		logger.debug("Analyzing method {}", method);

		SourcedType methodSource = new SourcedType(null, "Method " + method, null);

		SpringRequestInfo springRequestInfo = methodInfo.springRequestInfo;
		String methodPath = methodInfo.springRequestInfo.path;

		Iterable<HttpMethod> httpMethods = EnumSet.copyOf(methodInfo.springRequestInfo.methods);

		Collection<MethodParameter> parameters = new ArrayList<>();

		boolean usedMyOneGuess = false;
		for (Parameter p : method.getParameters()) {
			logger.debug("Analyzing parameter {}", p);

			MethodParameter mp = new MethodParameter();
			boolean hasName = p.isNamePresent();
			logger.debug("has name? {}", hasName);
			// code name is the name of the argument in Java
			mp.setCodeName(p.getName());
			if (hasName) {
				// network name is the name of the argument in the request - we're defaulting it here to the parameter name, but we may
				// override it after reading the parameters
				mp.setNetworkName(p.getName());
			}
			logger.debug("parameter code name now {} (from Java parameter name)", mp.getCodeName());
			mp.setType(p.getParameterizedType());
			if (p.isAnnotationPresent(PathVariable.class)) {
				PathVariable pathParam = p.getAnnotation(PathVariable.class);
				String pathVarName = pathParam.value();
				// Spring will auto-determine the path parameter based on ??
				if (StringUtils.isBlank(pathVarName)) {
					if (usedMyOneGuess) {
						// TODO I don't want to guess more than once since I don't understand Spring's logic yet
						pathVarName = "/* cannot auto-determine */";
					} else {
						pathVarName = determinePathVariable(springRequestInfo.path);
						usedMyOneGuess = true;
					}
				}
				if (!hasName && isValidJavaIdentifier(pathVarName)) {
					// if we don't have a user-specified code name yet, set it
					mp.setCodeName(pathVarName);
					logger.debug("parameter code name now {} (from @PathVariable)", mp.getCodeName());
				}
				if (!StringUtils.isBlank(pathVarName)) {
					// override network name from annotation
					mp.setNetworkName(pathVarName);
				}
				mp.setNetworkType(EndpointParameter.NetworkType.PATH);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(RequestParam.class)) {
				EndpointParameter.NetworkType netType;
				if (springRequestInfo.consumes == MediaType.APPLICATION_FORM_URLENCODED) {
					netType = EndpointParameter.NetworkType.FORM;
				} else {
					netType = EndpointParameter.NetworkType.QUERY;
				}
				RequestParam requestParam = p.getAnnotation(RequestParam.class);
				String reqParName = requestParam.value();
				if (!hasName && isValidJavaIdentifier(reqParName)) {
					// if we don't have a parameter name yet, set it
					mp.setCodeName(reqParName);
					logger.debug("parameter code name now {} (from @RequestParam)", mp.getCodeName());
				}
				if (!StringUtils.isBlank(reqParName)) {
					// override network name from annotation
					mp.setNetworkName(reqParName);
				}
				mp.setNetworkType(netType);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(RequestBody.class)) {
				mp.setNetworkType(EndpointParameter.NetworkType.JSON_BODY);
				parameters.add(mp);
			}
		}

		JType outType = null;
		if (methodInfo.producer) {
			PropertyEnumerator modeller = new JacksonTypeModeller(classOverrideHandler, defaultEnumType,
					options.isIncludeSubclasses() ? IncludeSubclasses.INCLUDE : IncludeSubclasses.EXCLUDE, packageNames);

			Type genericReturnType = method.getGenericReturnType();
			// in Spring, we want to check the return type to see if it extends HttpEntity. In the model, we are concerned about the body
			// which is going over the wire. Therefore, we unwrap it for the model
			if (genericReturnType instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) genericReturnType;
				Type rawType = pType.getRawType();
				if (rawType instanceof Class && HttpEntity.class.isAssignableFrom((Class<?>) rawType)) {
					// return type is HttpEntity<T>, so treat T as the model return type
					logger.debug("Method has a ResponseBody<...> return type, unwrapping for model");
					outType = modeller.readOneType(model,
							new SourcedType(pType.getActualTypeArguments()[0], "Unwrapped ResponseBody " + genericReturnType,
									methodSource));
				}
			} else if (genericReturnType instanceof Class) {
				if (genericReturnType == ResponseEntity.class) {
					// return type is HttpEntity (unspecialized), so we just treat the body as 'any'
					logger.debug("Method has a ResponseBody return type, unwrapping for model, returning 'any' (to be useful, you want to" +
							" parameterize this return type)");
					outType = new JAny();
				}
			}

			// if the above logic didn't unwrap a return type, just use the type as specified
			if (outType == null) {
				outType = modeller.readOneType(model, new SourcedType(genericReturnType, "Return type of method " + method, methodSource));
			}
		} else {
			PropertyEnumerator modeller = new JacksonTypeModeller(classOverrideHandler, defaultEnumType,
					options.isIncludeSubclasses() ? IncludeSubclasses.INCLUDE : IncludeSubclasses.EXCLUDE, packageNames);
			outType = modeller.readOneType(model, new SourcedType(String.class, "Return type of method " + method, methodSource));
		}


		for (HttpMethod httpMethod : httpMethods) {
			StringBuilder suffix = new StringBuilder();
			SuffixInfo suffixInfo = detector.getSuffixInfo(method, httpMethod);
			if (suffixInfo.isNeedsMethod()) {
				suffix.append(String.format("_%s", httpMethod.name()));
			}
			if (suffixInfo.getCount() != null) {
				suffix.append(String.format("_%d", suffixInfo.getCount()));
			}
			String endpointName = method.getDeclaringClass().getName() + "." + method.getName() + suffix;
			Endpoint endpoint = model.createEndpoint(endpointName);
			endpoint.setResponseBody(outType);
			endpoint.setPathTemplate(options.getUrlPrefix() + methodPath);
			endpoint.setConsumes(springRequestInfo.consumes == null ? null : springRequestInfo.consumes.toString());
			endpoint.setProduces(springRequestInfo.produces == null ? null : springRequestInfo.produces.toString());
			for (MethodParameter methodParameter : parameters) {
				PropertyEnumerator modeller = new JacksonTypeModeller(classOverrideHandler, defaultEnumType,
						options.isIncludeSubclasses() ? IncludeSubclasses.INCLUDE : IncludeSubclasses.EXCLUDE, packageNames);
				endpoint.getParameters()
						.add(new EndpointParameter(methodParameter.getCodeName(), methodParameter.getNetworkName(),
								modeller.readOneType(model, new SourcedType(methodParameter.getType(),
										"Parameter " + methodParameter.getCodeName() + " of method " + methodInfo.method, methodSource)),
								methodParameter.getNetworkType()));
			}
			endpoint.setMethod(httpMethod);

			// check validity
			ValidEndpointResponse validity = endpoint.isValid();
			if (!validity.ok) {
				logger.warn("Problems encountered while reading endpoint {}:", endpoint);
				for (String problem : validity.problems) {
					logger.warn("error: {}", problem);
				}
				model.removeEndpoint(endpoint);
			} else {
				logger.info("Added endpoint {} at {} method {}", endpointName, endpoint.getPathTemplate(), endpoint.getMethod());
			}
		}
	}

	private static String determinePathVariable(String path) {

		int start = path.indexOf("{");
		int end = path.indexOf("}", start);
		if (start < 0) {
			return "";
		} else if (end < 0) {
			return path.substring(start + 1);
		} else {
			return path.substring(start + 1, end);
		}
	}

	private SpringRequestInfo getMethodInfo(Method m) {
		logger.debug("Reading method {}", m);
		Annotation mappingAnn = getMappingAnnotation(m, m.getAnnotations());

		if (mappingAnn == null) {
			throw new RuntimeException("Could not find @RequestMapping annotation on method " + m);
		}

		AnnotationInstance method = annotationMap.getInstance(mappingAnn);
		logger.debug("Read method annotation {}", method);

		Class<?> toAdd = m.getDeclaringClass();
		Collection<AnnotationInstance> typeHierarchy = new ArrayList<>();
		while (toAdd != Object.class) {
			Annotation classMappingAnn = getMappingAnnotation(toAdd, toAdd.getAnnotations());
			if (classMappingAnn != null) {
				AnnotationInstance instance = annotationMap.getInstance(classMappingAnn);
				logger.debug("Read class annotation {}", method);
				typeHierarchy.add(instance);
			}
			toAdd = toAdd.getSuperclass();
		}

		Collection<AnnotationInstance> totalHierarchy = new ArrayList<>();
		totalHierarchy.add(method);
		totalHierarchy.addAll(typeHierarchy);

		final SpringRequestInfo result = new SpringRequestInfo();

		// per documentation: produces(): Supported at the type level as well as at the method level! When used at the type level, all
		// method-level mappings override this produces restriction.
		List<String> producesSet = null;
		for (AnnotationInstance inst : totalHierarchy) {
			if (!inst.getProduces().isEmpty()) {
				producesSet = inst.getProduces();
				break;
			}
		}

		// per documentation: consumes(): Supported at the type level as well as at the method level! When used at the type level, all
		// method-level mappings override this consumes restriction.
		List<String> consumesSet = null;
		for (AnnotationInstance inst : totalHierarchy) {
			if (!inst.getConsumes().isEmpty()) {
				consumesSet = inst.getConsumes();
				break;
			}
		}

		// per documentation: method(): Supported at the type level as well as at the method level! When used at the type level, all
		// method-level mappings inherit this HTTP method restriction (i.e. the type-level restriction gets checked before the handler
		// method is even resolved).
		List<RequestMethod> methodSet = null;
		for (AnnotationInstance inst : typeHierarchy) {
			if (!inst.getMethod().isEmpty()) {
				methodSet = inst.getMethod();
				break;
			}
		}
		// if we didn't find it on the type, go with the method
		if (methodSet == null) {
			methodSet = method.getMethod();
		}

		// per documentation: Supported at the type level as well as at the method level! When used at the type level, all method-level
		// mappings inherit this primary mapping, narrowing it for a specific handler method.
		// seems to be actually more than this - superclasses are brought into it as well
		String path = null;
		for (AnnotationInstance inst : totalHierarchy) {
			if (!inst.getPath().isEmpty()) {
				String pathFragment = inst.getPath().get(0); // choose one of the path options arbitrarily
				if (pathFragment.endsWith("testServicesString")) {
					logger.debug("Here");
				}
				path = joinPaths(pathFragment, path);
			}
		}

		if (methodSet == null || methodSet.isEmpty()) {
			result.validity = "Could not determine HTTP method (not in method or class annotation): " + m;
			return result;
		} else {
			// translate HTTP methods and filter out the ones irrelevant to JSON
			Set<HttpMethod> methods = EnumSet.noneOf(HttpMethod.class);
			for (RequestMethod springMethod : methodSet) {
				HttpMethod hMethod = httpMethodMap.get(springMethod);
				if (hMethod != null && jsonnableMethods.contains(hMethod)) {
					methods.add(hMethod);
				}
			}
			if (!methods.isEmpty()) {
				result.methods = methods;
			}
		}

		BiConsumer<SpringRequestInfo, String> producesTest = (result1, contentType) -> {
			if (contentType != null) {
				if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
					result.produces = MediaType.APPLICATION_JSON;
				}
			}
		};

		if (producesSet != null && !producesSet.isEmpty()) {
			for (String contentType : producesSet) {
				producesTest.accept(result, contentType);
			}
		} else {
			producesTest.accept(result, options.getDefaultContentType());
		}

		BiConsumer<SpringRequestInfo, String> consumesTest = (result1, contentType) -> {
			if (contentType != null) {
				if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
					assert result1.consumes == null || result1.consumes == MediaType.APPLICATION_JSON;
					result1.consumes = MediaType.APPLICATION_JSON;
				}
				if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
					assert result1.consumes == null || result1.consumes == MediaType.APPLICATION_FORM_URLENCODED;
					result1.consumes = MediaType.APPLICATION_FORM_URLENCODED;
				}
			}
		};

		if (consumesSet != null && !consumesSet.isEmpty()) {
			for (String contentType : consumesSet) {
				consumesTest.accept(result, contentType);
			}
		} else {
			consumesTest.accept(result, options.getDefaultContentType());
		}

		if (StringUtils.isBlank(path)) {
			result.validity = "No path specified for method (not in method or class annotation): " + m;
		} else {
			result.path = path;
		}
		return result;
	}

	private Annotation getMappingAnnotation(Object m, Annotation[] annotations) {
		Annotation mappingAnn = null;
		for (Annotation ann : annotations) {
			if (annotationMap.containsAnnotation(ann.annotationType())) {
				if (mappingAnn == null) {
					mappingAnn = ann;
				} else {
					logger.warn("Found another RequestMapping annotation on method {}: {}. It will not override the already-found {}", m,
							ann, mappingAnn);
				}
			}
		}
		return mappingAnn;
	}

	@Override
	public void model(Model model) {
		assert options != null;
		assert options.getPackages() != null;

		this.model = model;
		createModel(options.getPackages().toArray(new String[0]));
	}

	@Override
	public Class<Options> getOptionsClass() {
		return Options.class;
	}

	@Override
	public void acceptOptions(Object options, List<? super String> errors) {
		this.options = (Options) options;
		classOverrideHandler.ingestOverrides(this.options.getClassSubstitutions());
		if (this.options.getPackages() == null || this.options.getPackages().isEmpty()) {
			errors.add("Package name to process is required.");
		} else {
			if (this.options.isDefaultStringEnums()) {
				defaultEnumType = JEnum.EnumType.STRING;
			} else {
				defaultEnumType = JEnum.EnumType.NUMERIC;
			}
			packageNames = this.options.getPackages().toArray(new String[0]);
		}
	}
}

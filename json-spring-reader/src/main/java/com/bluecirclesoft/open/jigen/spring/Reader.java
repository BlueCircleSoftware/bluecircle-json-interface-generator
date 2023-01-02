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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bluecirclesoft.open.jigen.ClassOverrideHandler;
import com.bluecirclesoft.open.jigen.ModelCreator;
import com.bluecirclesoft.open.jigen.annotations.Generate;
import com.bluecirclesoft.open.jigen.jacksonModeller.JacksonTypeModeller;
import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.EndpointParameter;
import com.bluecirclesoft.open.jigen.model.HttpMethod;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.model.ValidEndpointResponse;
import com.bluecirclesoft.open.jigen.spring.springmodel.AnnotationInstance;
import com.bluecirclesoft.open.jigen.spring.springmodel.GlobalAnnotationMap;

/**
 * TODO document me
 */
public class Reader implements ModelCreator<Options> {

	private static final Logger logger = LoggerFactory.getLogger(Reader.class);

	private static final Map<RequestMethod, HttpMethod> httpMethodMap = new HashMap<>();

	private static final Set<HttpMethod> jsonnableMethods = new HashSet<>();

	private static class MethodInfo {

		boolean consumer;

		boolean producer;

		Method method;

		SpringRequestInfo springRequestInfo;

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

	private ClassOverrideHandler classOverrideHandler = new ClassOverrideHandler();

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

	private static boolean isValidJavaIdentifier(String value) {
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

	private Model createModel(String... packageNames) {

		assert packageNames != null;
		assert packageNames.length > 0;

		annotationMap.ingestAnnotations(packageNames);

		Map<Method, MethodInfo> annotatedMethods = new HashMap<>();
		for (String packageName : packageNames) {
			Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packageName))
					.setScanners(new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner()));

			Set<Method> allMethods = findRequestMappingMethods(reflections);
			for (Method method : allMethods) {
				SpringRequestInfo springInfo = getMethodInfo(method);
				if (springInfo.methods == null || springInfo.methods.isEmpty()) {
					// method had no interesting HTTP methods
					continue;
				}
				logger.info("Handling method {}, Spring info is {}", method, springInfo);
				if (springInfo.validity != null) {
					logger.warn("Problems encountered while reading method {}:", method);
					logger.warn("error: {}", springInfo.validity);
					continue;
				}
				boolean producer = isProducer(method, springInfo);
				boolean consumer = isConsumer(method, springInfo);
				if (producer || consumer) {
					MethodInfo newMethodInfo = annotatedMethods.computeIfAbsent(method, (m) -> new MethodInfo(m, springInfo));
					newMethodInfo.producer = producer;
					newMethodInfo.consumer = consumer;
				}
			}

			for (Class<?> generatedClass : findClassesTaggedGenerate(reflections)) {
				JacksonTypeModeller modeller =
						new JacksonTypeModeller(classOverrideHandler, defaultEnumType, options.isIncludeSubclasses(), packageNames);
				modeller.readOneType(model, generatedClass);
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

		return model;
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
		SpringRequestInfo springRequestInfo = methodInfo.springRequestInfo;
		Method method = methodInfo.method;
		String methodPath = methodInfo.springRequestInfo.path;

		Set<HttpMethod> httpMethods = new HashSet<>(methodInfo.springRequestInfo.methods);

		List<MethodParameter> parameters = new ArrayList<>();

		boolean usedMyOneGuess = false;
		for (Parameter p : method.getParameters()) {
			MethodParameter mp = new MethodParameter();
			boolean hasName = p.isNamePresent();
			mp.setCodeName(p.getName());
			mp.setType(p.getParameterizedType());
			if (p.isAnnotationPresent(PathVariable.class)) {
				PathVariable pathParam = p.getAnnotation(PathVariable.class);
				String pp = pathParam.value();
				// Spring will auto-determine the path parameter based on ??
				if (StringUtils.isBlank(pp)) {
					if (usedMyOneGuess) {
						// TODO I don't want to guess more than once since I don't understand Spring's logic yet
						pp = "/* cannot auto-determine */";
					} else {
						pp = determinePathVariable(springRequestInfo.path);
						usedMyOneGuess = true;
					}
				}
				if (!hasName && isValidJavaIdentifier(pp)) {
					mp.setCodeName(pp);
				}
				mp.setNetworkName(pp);
				mp.setNetworkType(EndpointParameter.NetworkType.PATH);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(RequestParam.class)) {
				EndpointParameter.NetworkType netType;
				if (springRequestInfo.consumes == MediaType.APPLICATION_FORM_URLENCODED) {
					netType = EndpointParameter.NetworkType.FORM;
				} else {
					netType = EndpointParameter.NetworkType.QUERY;
				}
				RequestParam queryParam = p.getAnnotation(RequestParam.class);
				if (!hasName && isValidJavaIdentifier(queryParam.value())) {
					mp.setCodeName(queryParam.value());
				}
				mp.setNetworkName(queryParam.value());
				mp.setNetworkType(netType);
				parameters.add(mp);
			} else if (p.isAnnotationPresent(RequestBody.class)) {
				mp.setNetworkType(EndpointParameter.NetworkType.JSON_BODY);
				parameters.add(mp);
			}
		}

		JType outType;
		if (methodInfo.producer) {
			JacksonTypeModeller modeller =
					new JacksonTypeModeller(classOverrideHandler, defaultEnumType, options.isIncludeSubclasses(), packageNames);
			outType = modeller.readOneType(model, method.getGenericReturnType());
		} else {
			JacksonTypeModeller modeller =
					new JacksonTypeModeller(classOverrideHandler, defaultEnumType, options.isIncludeSubclasses(), packageNames);
			outType = modeller.readOneType(model, String.class);
		}


		for (HttpMethod httpMethod : httpMethods) {
			String suffix = "";
			SuffixInfo suffixInfo = detector.getSuffixInfo(method, httpMethod);
			if (suffixInfo.isNeedsMethod()) {
				suffix = suffix + "_" + httpMethod.name();
			}
			if (suffixInfo.getCount() != null) {
				suffix = suffix + "_" + suffixInfo.getCount();
			}
			Endpoint endpoint = model.createEndpoint(method.getDeclaringClass().getName() + "." + method.getName() + suffix);
			endpoint.setResponseBody(outType);
			endpoint.setPathTemplate(methodPath);
			endpoint.setConsumes(springRequestInfo.consumes == null ? null : springRequestInfo.consumes.toString());
			endpoint.setProduces(springRequestInfo.produces == null ? null : springRequestInfo.produces.toString());
			for (MethodParameter pathParam : parameters) {
				JacksonTypeModeller modeller =
						new JacksonTypeModeller(classOverrideHandler, defaultEnumType, options.isIncludeSubclasses(), packageNames);
				endpoint.getParameters()
						.add(new EndpointParameter(pathParam.getCodeName(), pathParam.getNetworkName(),
								modeller.readOneType(model, pathParam.getType()), pathParam.getNetworkType()));
			}
			endpoint.setMethod(httpMethod);

			// check validity
			ValidEndpointResponse validity = endpoint.isValid();
			if (!validity.ok) {
				logger.warn("Problems encountered while reading method {}:", method);
				for (String problem : validity.problems) {
					logger.warn("error: {}", problem);
				}
				model.removeEndpoint(endpoint);
			}
		}
	}

	private String determinePathVariable(String path) {

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
		logger.info("Reading method {}", m);
		Annotation mappingAnn = getMappingAnnotation(m, m.getAnnotations());

		if (mappingAnn == null) {
			throw new RuntimeException("Cound not find @RequestMapping annotation on method " + m);
		}

		AnnotationInstance method = annotationMap.getInstance(mappingAnn);
		logger.info("Read method annotation {}", method);

		Class toAdd = m.getDeclaringClass();
		List<AnnotationInstance> typeHierarchy = new ArrayList<>();
		while (toAdd != Object.class) {
			Annotation classMappingAnn = getMappingAnnotation(toAdd, toAdd.getAnnotations());
			if (classMappingAnn != null) {
				AnnotationInstance instance = annotationMap.getInstance(classMappingAnn);
				logger.info("Read class annotation {}", method);
				typeHierarchy.add(instance);
			}
			toAdd = toAdd.getSuperclass();
		}

		List<AnnotationInstance> totalHierarchy = new ArrayList<>();
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
					logger.info("Here");
				}
				path = joinPaths(pathFragment, path);
			}
		}

		if (methodSet == null || methodSet.isEmpty()) {
			result.validity = "Could not determine HTTP method (not in method or class annotation): " + m;
			return result;
		} else {
			// translate HTTP methods and filter out the ones irrelevant to JSON
			Set<HttpMethod> methods = new HashSet<>();
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

		if (producesSet != null && producesSet.size() > 0) {
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

		if (consumesSet != null && consumesSet.size() > 0) {
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
					logger.warn("Found another RequestMapping annotation on method {}: {}. It will not override the already-found {}",
							new Object[]{m, ann, mappingAnn});
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
	public void acceptOptions(Options options, List<String> errors) {
		this.options = options;
		classOverrideHandler.ingestOverrides(options.getClassSubstitutions());
		if (options.getPackages() == null || options.getPackages().isEmpty()) {
			errors.add("Package name to process is required.");
		} else {
			if (options.isDefaultStringEnums()) {
				defaultEnumType = JEnum.EnumType.STRING;
			} else {
				defaultEnumType = JEnum.EnumType.NUMERIC;
			}
			packageNames = options.getPackages().toArray(new String[0]);
		}
	}
}

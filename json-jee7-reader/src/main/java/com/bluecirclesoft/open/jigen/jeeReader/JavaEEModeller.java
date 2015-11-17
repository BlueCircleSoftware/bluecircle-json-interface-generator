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

import com.bluecirclesoft.open.jigen.helper.InsertingMap;
import com.bluecirclesoft.open.jigen.inputJackson.JacksonTypeModeller;
import com.bluecirclesoft.open.jigen.model.Model;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.HashMap;

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

	private Model model;

	private String urlPrefix;

	public Model createModel(String urlPrefix, String... packageNames) {
		InsertingMap<Method, MethodInfo> annotatedMethods =
				new InsertingMap<>(new HashMap<>(), MethodInfo::new);
		for (String packageName : packageNames) {
			Reflections reflections = new Reflections(
					new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packageName))
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

	private void readMethod(MethodInfo methodInfo) {
		Method method = methodInfo.method;
		Path methodPath = method.getAnnotation(Path.class);
		Path classPath = method.getDeclaringClass().getAnnotation(Path.class);


		if (methodInfo.consumer) {
			if (method.getParameterCount() > 1) {
				logger.warn("Cannot consume multiple JSON objects - not supported");
				return;
			}
			JacksonTypeModeller modeller = new JacksonTypeModeller();
			modeller.enumerateProperties(model, method.getGenericParameterTypes()[0]);
		} else {
			// TODO - string or something
		}

		if (methodInfo.producer) {
			JacksonTypeModeller modeller = new JacksonTypeModeller();
			modeller.enumerateProperties(model, method.getGenericReturnType());
		} else {
			// TODO - string or something
		}

	}

	public Model getModel() {
		return model;
	}
}

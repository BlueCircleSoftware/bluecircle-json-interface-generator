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

package com.bluecirclesoft.open.jigen.spring.springmodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AliasFor;

/**
 * TODO document me
 */
public class MappingAnnotation {

	private static final Logger log = LoggerFactory.getLogger(MappingAnnotation.class);

	private final Class<? extends Annotation> annotationClass;

	private AnnotationInstance defaults;

	// field specified in annotation -> other fields I should set
	private final Map<String, Set<String>> aliasMap = new HashMap<>();

	private MappingAnnotation parent;

	private boolean filling;

	private boolean filled;

	public MappingAnnotation(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	public static String namify(Class annotationClass) {
		return annotationClass.getPackage().getName().equals("org.springframework.web.bind.annotation") ? annotationClass.getSimpleName() :
				annotationClass.getName();
	}

	public void fill(GlobalAnnotationMap globalAnnotationMap) {
		if (filled) {
			return;
		}
		try {
			if (filling) {
				throw new RuntimeException("Circular reference");
			}
			filling = true;
			log.debug("Filling {}", annotationClass);
			boolean foundMapping = false;
			for (Annotation ann : annotationClass.getAnnotations()) {
				if (globalAnnotationMap.containsAnnotation(ann.annotationType())) {
					if (foundMapping) {
						log.warn("RequestMapping annotation " + ann + " found on annotation class " + annotationClass + ", but another " +
								"RequestMapping annotation " + parent.getAnnotationClass() + " was already found - ignoring this class");
					} else {
						foundMapping = true;
						log.debug("Looking at annotation {}", ann);
						defaults = globalAnnotationMap.getInstance(ann);
						parent = globalAnnotationMap.getAnnotation(ann.annotationType());
					}
				}
			}
			Method[] methods = annotationClass.getDeclaredMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(AliasFor.class)) {
					AliasFor alias = method.getAnnotation(AliasFor.class);
					// don't prefix Spring annotations with package name
					String className = namify(annotationClass);
					String key = className + "." + method.getName();
					Set<String> aliasSet = aliasMap.computeIfAbsent(key, (x) -> new HashSet<>());
					if (StringUtils.isNotBlank(alias.value())) {
						aliasSet.add(className + "." + alias.value());
					}
					if (StringUtils.isNotBlank(alias.attribute())) {
						aliasSet.add(className + "." + alias.attribute());
					}
					if (alias.annotation() != Annotation.class) {
						String parentName = namify(alias.annotation()) + "." + method.getName();
						aliasSet.add(parentName);
						if (parent != null && parent.getAliasMap().containsKey(parentName)) {
							aliasSet.addAll(parent.getAliasMap().get(parentName));
						}
					}
				}
			}
			filling = false;
			filled = true;
		} catch (Throwable t) {
			log.error("Exception filling annotation info for {}", annotationClass);
			throw t;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("MappingAnnotation{");
		sb.append("\n  annotationClass=").append(annotationClass);
		sb.append(",\n  defaults=").append(defaults);
		sb.append(",\n  aliasMap=").append(aliasMap);
		sb.append(",\n  parent=").append(parent);
		sb.append(",\n  filling=").append(filling);
		sb.append(",\n  filled=").append(filled);
		sb.append("\n}");
		return sb.toString();
	}

	public AnnotationInstance createInstance(Annotation ann, GlobalAnnotationMap map) {
		AnnotationInstance instance = new AnnotationInstance(map.getAnnotation(ann.annotationType()));
		instance.ingest(ann, map);
		return instance;
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return annotationClass;
	}

	public AnnotationInstance getDefaults() {
		return defaults;
	}

	public Map<String, Set<String>> getAliasMap() {
		return aliasMap;
	}

	public MappingAnnotation getParent() {
		return parent;
	}

	public boolean isFilling() {
		return filling;
	}

	public boolean isFilled() {
		return filled;
	}

	public Set<String> getAliasesFor(String s) {
		Set<String> res = aliasMap.get(s);
		if (res != null) {
			return res;
		} else {
			return Collections.emptySet();
		}
	}
}

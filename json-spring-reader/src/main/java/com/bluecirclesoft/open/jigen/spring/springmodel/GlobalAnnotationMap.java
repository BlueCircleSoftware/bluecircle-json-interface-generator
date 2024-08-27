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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO document me
 */
public class GlobalAnnotationMap {

	private static final Logger log = LoggerFactory.getLogger(GlobalAnnotationMap.class);

	private final Map<Class<? extends Annotation>, MappingAnnotation> map = new LinkedHashMap<>();

	public void ingestAnnotations(String... packageNamesArr) {

		Collection<String> packageNames = new ArrayList<>();
		packageNames.add("org.springframework");
		packageNames.addAll(Arrays.asList(packageNamesArr));
		boolean foundNew;
		Map<String, Reflections> scannerCache = new HashMap<>();
		do {
			foundNew = false;
			for (String packageName : packageNames) {
				log.debug("Scanning package {} for @RequestMappings", packageName);
				Reflections reflections = scannerCache.computeIfAbsent(packageName, (p) -> new Reflections(
						new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(p))
								.setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)));

				Deque<Class<?>> finderQueue = new ArrayDeque<>();
				finderQueue.add(RequestMapping.class);
				for (Class<? extends Annotation> ann : map.keySet()) {
					finderQueue.addAll(reflections.getTypesAnnotatedWith(ann));
				}

				while (!finderQueue.isEmpty()) {
					Class<?> next = finderQueue.poll();
					if (next == null) {
						// queue empty
						break;
					}
					if (!(Annotation.class.isAssignableFrom(next))) {
						// not an annotation
						continue;
					}
					Class<? extends Annotation> nextAsAnn = (Class<? extends Annotation>) next;
					if (map.containsKey(nextAsAnn)) {
						// already processed
						continue;
					}
					foundNew = true;
					log.debug("Processing annotation {}", nextAsAnn.getName());
					MappingAnnotation mappingAnnotation = new MappingAnnotation(nextAsAnn);
					map.put(nextAsAnn, mappingAnnotation);
					// find other stuff annotated with this annotation, and add it to the queue
					finderQueue.addAll(reflections.getTypesAnnotatedWith(nextAsAnn));
				}
			}
		} while (foundNew);

		for (Map.Entry<Class<? extends Annotation>, MappingAnnotation> annClass : map.entrySet()) {
			annClass.getValue().fill(this);
			log.debug("Filled annotation now {}", annClass.getValue());
		}
	}

	public boolean containsAnnotation(Class<? extends Annotation> annClass) {
		return map.containsKey(annClass);
	}

	public MappingAnnotation getAnnotation(Class<? extends Annotation> annClass) {
		MappingAnnotation ma = map.get(annClass);
		if (ma == null) {
			throw new RuntimeException("Internal error: annotation " + annClass + " was not found during ingest");
		}
		ma.fill(this);
		return ma;
	}

	public AnnotationInstance getInstance(Annotation ann) {
		MappingAnnotation ma = getAnnotation(ann.annotationType());
		return MappingAnnotation.createInstance(ann, this);
	}

	public Set<Class<? extends Annotation>> getAllAnnotations() {
		return map.keySet();
	}
}

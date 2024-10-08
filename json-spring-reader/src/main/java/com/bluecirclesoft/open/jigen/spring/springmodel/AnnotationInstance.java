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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO document me
 */
public class AnnotationInstance {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationInstance.class);

	private final MappingAnnotation baseAnnotation;

	@Setter
	@Getter
	private String name;

	@Getter
	private final List<String> path = new ArrayList<>();

	@Getter
	private final List<RequestMethod> method = new ArrayList<>();

	@Getter
	private final List<String> consumes = new ArrayList<>();

	@Getter
	private final List<String> produces = new ArrayList<>();

	public AnnotationInstance(MappingAnnotation base) {
		this.baseAnnotation = base;
	}

	public void ingest(Annotation myAnnotation, GlobalAnnotationMap globalAnnotationMap) {
		for (Method method : myAnnotation.annotationType().getMethods()) {
			if (method.getParameterCount() == 0) {
				try {
					Object val = method.invoke(myAnnotation);
					String name = MappingAnnotation.namify(myAnnotation.annotationType()) + "." + method.getName();
					add(name, val);
					for (String alias : baseAnnotation.getAliasesFor(
							MappingAnnotation.namify(myAnnotation.annotationType()) + "." + method.getName())) {
						add(alias, val);
					}
				} catch (Exception e) {
					logger.warn("Got exception invoking method {}; ignoring", method, e);
				}
			}
		}
		// fill defaults for anything not set
		if (baseAnnotation.getDefaults() != null) {
			AnnotationInstance def = baseAnnotation.getDefaults();
			if (StringUtils.isBlank(this.name)) {
				this.name = def.getName();
			}
			if (this.path.isEmpty()) {
				this.path.addAll(def.getPath());
			}
			if (this.method.isEmpty()) {
				this.method.addAll(def.getMethod());
			}
			if (this.consumes.isEmpty()) {
				this.consumes.addAll(def.getConsumes());
			}
			if (this.produces.isEmpty()) {
				this.produces.addAll(def.getProduces());
			}
		}
	}

	public void add(String field, Object thingOrCollection) {
		if (thingOrCollection instanceof Collection) {
			Iterable<?> coll = (Collection<?>) thingOrCollection;
			for (Object thing : coll) {
				add(field, thing);
			}
		} else if (thingOrCollection instanceof Object[]) {
			Object[] arr = (Object[]) thingOrCollection;
			for (Object thing : arr) {
				add(field, thing);
			}
		} else {
			switch (field) {
				case "RequestMapping.name":
					this.name = (String) thingOrCollection;
					break;
				case "RequestMapping.path":
					this.path.add((String) thingOrCollection);
					break;
				case "RequestMapping.method":
					this.method.add((RequestMethod) thingOrCollection);
					break;
				case "RequestMapping.consumes":
					this.consumes.add((String) thingOrCollection);
					break;
				case "RequestMapping.produces":
					this.produces.add((String) thingOrCollection);
					break;
				default:
					logger.debug("Ignoring set request on field {}", field);
			}
		}
	}

	@Override
	public String toString() {
		return "AnnotationInstance{" + "baseAnnotation=" + baseAnnotation + ", name='" + name + '\'' + ", path=" + path + ", method=" +
				method + ", consumes=" + consumes + ", produces=" + produces + '}';
	}
}

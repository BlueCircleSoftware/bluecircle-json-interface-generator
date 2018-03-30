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

package com.bluecirclesoft.open.jigen.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO document me
 */
class MappingAnnotationVisitorByString<T> implements MappingAnnotationVisitor {

	private final String methodName;

	private final Function<T, Boolean> apply;

	public MappingAnnotationVisitorByString(String methodName, Function<T, Boolean> apply) {
		this.methodName = methodName;
		this.apply = apply;
	}

	private boolean applyOnce(Annotation ann) {
		try {
			Method method = ann.annotationType().getMethod(this.methodName);
			method.setAccessible(true);
			T annotationVal = (T) method.invoke(ann);
			return this.apply.apply(annotationVal);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean visit(RequestMapping mapping) {
		return applyOnce(mapping);
	}

	public boolean visit(DeleteMapping mapping) {
		return applyOnce(mapping);
	}

	public boolean visit(GetMapping mapping) {
		return applyOnce(mapping);
	}

	public boolean visit(PatchMapping mapping) {
		return applyOnce(mapping);
	}

	public boolean visit(PostMapping mapping) {
		return applyOnce(mapping);
	}

	public boolean visit(PutMapping mapping) {
		return applyOnce(mapping);
	}
}

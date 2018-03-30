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

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO document me
 */
interface MappingAnnotationVisitor {

	boolean visit(RequestMapping mapping);

	boolean visit(DeleteMapping mapping);

	boolean visit(GetMapping mapping);

	boolean visit(PatchMapping mapping);

	boolean visit(PostMapping mapping);

	boolean visit(PutMapping mapping);

	default void visitAll(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof RequestMapping) {
				if (this.visit((RequestMapping) annotation)) {
					break;
				}
			} else if (annotation instanceof DeleteMapping) {
				final DeleteMapping mapping = (DeleteMapping) annotation;
				if (this.visit(mapping)) {
					break;
				}
			} else if (annotation instanceof GetMapping) {
				final GetMapping mapping = (GetMapping) annotation;
				if (this.visit(mapping)) {
					break;
				}
			} else if (annotation instanceof PatchMapping) {
				final PatchMapping mapping = (PatchMapping) annotation;
				if (this.visit(mapping)) {
					break;
				}
			} else if (annotation instanceof PostMapping) {
				final PostMapping mapping = (PostMapping) annotation;
				if (this.visit(mapping)) {
					break;
				}
			} else if (annotation instanceof PutMapping) {
				final PutMapping mapping = (PutMapping) annotation;
				if (this.visit(mapping)) {
					break;
				}
			}
		}
	}

}

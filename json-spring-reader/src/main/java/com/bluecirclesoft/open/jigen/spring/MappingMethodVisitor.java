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

import java.util.function.Function;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * TODO document me
 */
class MappingMethodVisitor implements MappingAnnotationVisitor {

	private final Function<RequestMethod[], Boolean> processMethod;

	public MappingMethodVisitor(Function<RequestMethod[], Boolean> processMethod) {
		this.processMethod = processMethod;
	}

	@Override
	public boolean visit(RequestMapping mapping) {
		return processMethod.apply(mapping.method());
	}

	@Override
	public boolean visit(DeleteMapping mapping) {
		return processMethod.apply(new RequestMethod[]{RequestMethod.DELETE});
	}

	@Override
	public boolean visit(GetMapping mapping) {
		return processMethod.apply(new RequestMethod[]{RequestMethod.GET});
	}

	@Override
	public boolean visit(PatchMapping mapping) {
		return processMethod.apply(new RequestMethod[]{RequestMethod.PATCH});
	}

	@Override
	public boolean visit(PostMapping mapping) {
		return processMethod.apply(new RequestMethod[]{RequestMethod.POST});
	}

	@Override
	public boolean visit(PutMapping mapping) {
		return processMethod.apply(new RequestMethod[]{RequestMethod.PUT});
	}
}

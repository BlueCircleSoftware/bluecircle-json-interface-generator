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

package com.bluecirclesoft.open.jigen.spring;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Service with overloaded method names.
 */
@RequestMapping(path = "/overload")
public class OverloadedService {

	@GetMapping(path = "/one", produces = MediaType.APPLICATION_JSON_VALUE)
	public Person fetch(@RequestParam("id") int id) {
		return null;
	}

	@GetMapping(path = "/two", produces = MediaType.APPLICATION_JSON_VALUE)
	public Person fetch(@RequestParam("name") String name) {
		return null;
	}
}

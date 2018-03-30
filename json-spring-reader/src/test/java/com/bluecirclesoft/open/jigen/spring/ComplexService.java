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

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * TODO document me
 */
@RequestMapping(value = "/complex", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ComplexService {

	@RequestMapping(path = "/getMapSS", method = RequestMethod.GET)
	public Map<String, String> getMapSS() {
		return new HashMap<>();
	}

	@RequestMapping(path = "/getVoid", method = RequestMethod.GET)
	public void getVoid() {

	}

	@GetMapping(path = "/getVoid2/{a}/{b}")
	public void getVoid2(@PathVariable("a") int a, @PathVariable("b") String b) {

	}
}

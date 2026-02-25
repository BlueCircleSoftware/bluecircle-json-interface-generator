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
 */

package com.bluecirclesoft.open.jigen.integrationSpring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Service for Spring reader integration edge cases.
 */
@RequestMapping(path = {"/readerEdgeA", "/readerEdgeB"})
@CrossOrigin
@Component
public class ReaderEdgeService {

	@RequestMapping(path = "/defaultMethod", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	@ResponseBody
	public MyStringList defaultMethod() {
		return makeList("default", 1);
	}

	@GetMapping(path = {"/multiOne", "/multiTwo"}, produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	@ResponseBody
	public MyStringList multiPaths() {
		return makeList("multi", 1);
	}

	@PostMapping(path = "/vendorJson",
			consumes = "application/vnd.test+json",
			produces = "application/json;charset=UTF-8",
			headers = "Accept=*/*")
	@ResponseBody
	public JsonResponse vendorJson(@RequestBody JsonRequest request) {
		JsonResponse response = new JsonResponse();
		response.setDoubleA(request.getA() + request.getA());
		response.setDoubleB(request.getB() + request.getB());
		response.setDoubleBoth(request.getA() + request.getB() + request.getA() + request.getB());
		return response;
	}

	private static MyStringList makeList(String x, int i) {
		List<String> result = new ArrayList<>(i);
		for (int j = 0; j < i; j++) {
			result.add(x);
		}
		MyStringList msl = new MyStringList();
		msl.setList(result.toArray(new String[0]));
		return msl;
	}
}

/*
 * Copyright 2017 Blue Circle Software, LLC
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
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Services that return String
 */
@RequestMapping(path = "/testServicesString")
@CrossOrigin
@Component
public class TestServicesString {

	private static final Logger log = LoggerFactory.getLogger(TestServicesString.class);

	@GetMapping(path = "/serviceCheck", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList serviceCheck() {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("Service check invoked");
		return makeList("Service check successful", 1);
	}

	@GetMapping(path = "/doubleUpGetQ", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleUpGetQ(@RequestParam("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList("\"" + x + x + "\"", 1);
	}

	@GetMapping(path = "/doubleUpGetP/{x}", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleUpGetP(@PathVariable("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info(" x = {}", x);
		return makeList("\"" + x + x + "\"", 1);
	}

	@PostMapping(path = "/doubleUpPostQ",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleUpPostQ(@RequestParam("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList("\"" + x + x + "\"", 1);
	}

	@PostMapping(path = "/doubleUpPostF", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleUpPostF(@RequestParam("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList("\"" + x + x + "\"", 1);
	}

	@PostMapping(path = "/doubleUpPostP/{x}", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleUpPostP(@PathVariable("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList("\"" + x + x + "\"", 1);
	}

	@PostMapping(path = "/doubleUpPostPNoVar/{x}", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleUpPostPNoVar(String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList("\"" + x + x + "\"", 1);
	}

	@GetMapping(path = "/doubleArrGetQ", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleArrGetQ(@RequestParam("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList(x, 3);
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

	@GetMapping(path = "/doubleArrGetP/{x}", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleArrGetP(@PathVariable("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList(x, 3);
	}

	@PostMapping(path = "/doubleArrPostQ",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleArrPostQ(@RequestParam("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList(x, 3);
	}

	@PostMapping(path = "/doubleArrPostP/{x}", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleArrPostP(@PathVariable("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList(x, 3);
	}

	@PostMapping(path = "/doubleArrPostF", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	public @ResponseBody
	MyStringList doubleArrPostF(@RequestParam("x") String x) {
		log.info("Method: {}", CallerFinder.getMyName());
		log.info("x = {}", x);
		return makeList(x, 3);
	}
}

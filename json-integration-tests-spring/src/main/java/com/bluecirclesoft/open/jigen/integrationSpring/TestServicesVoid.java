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

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Services that return String
 */
@RequestMapping("/testServicesVoid")
@CrossOrigin
@Component
public class TestServicesVoid {

	private static final Logger log = LoggerFactory.getLogger(TestServicesVoid.class);

	@GetMapping(path = "/voidGetQ", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void voidGetQ(@RequestParam("x") String x, HttpServletResponse response, VoidTestState voidTestState) {
		log.info("Inside doubleUpGetQ, x = {}", x);
		voidTestState.addToTotalString(x);
	}

	@GetMapping(path = "/voidGetP/{x}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void doubleUpGetP(@PathVariable("x") String x, HttpServletResponse response, VoidTestState voidTestState) {
		log.info("Inside doubleUpGetP, x = {}", x);
		voidTestState.addToTotalString(x);
	}

	@PostMapping(path = "/voidPostQ", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void doubleUpPostQ(@RequestParam("x") String x, HttpServletResponse response, VoidTestState voidTestState) {
		log.info("Inside doubleUpPostQ, x = {}", x);
		voidTestState.addToTotalString(x);
	}

	@PostMapping(path = "/voidPostP/{x}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void doubleUpPostP(@PathVariable("x") String x, HttpServletResponse response, VoidTestState voidTestState) {
		log.info("Inside doubleUpPostP, x = {}", x);
		voidTestState.addToTotalString(x);
	}

	@PostMapping(path = "/voidPostF/{x}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void doubleUpPostF(@RequestParam("x") String x, HttpServletResponse response, VoidTestState voidTestState) {
		log.info("Inside doubleUpPostP, x = {}", x);
		voidTestState.addToTotalString(x);
	}

	@GetMapping(path = "/getTotal", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getTotal(VoidTestState voidTestState) {
		return '\"' + voidTestState.getTotalString() + '\"';
	}
}

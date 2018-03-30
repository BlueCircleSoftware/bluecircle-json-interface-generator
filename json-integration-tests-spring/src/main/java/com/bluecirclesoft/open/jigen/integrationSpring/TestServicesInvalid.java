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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Services that are invalid in some form or fashion
 */
@Component
@RequestMapping(path = "/testServicesInvalid")
public class TestServicesInvalid {

	private static final Logger log = LoggerFactory.getLogger(TestServicesInvalid.class);

	@Autowired
	private HttpServletResponse response;

	private void setCORSHeaders() {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT");
	}

	/**
	 * WildFly complains "RESTEASY003065: Cannot consume content type".
	 */
	@PostMapping(path = "/doubleUpPostF", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String doubleUpPostF(@RequestParam("x") String x) {
		log.info("Inside doubleUpPostQ, x = {}", x);
		setCORSHeaders();
		return "\"" + x + x + "\"";
	}

}

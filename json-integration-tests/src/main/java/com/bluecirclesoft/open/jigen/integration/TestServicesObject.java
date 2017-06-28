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

package com.bluecirclesoft.open.jigen.integration;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test services that consume/produce objects
 */
@Path("/testServicesObject")
@Stateless
public class TestServicesObject {

	private static final Logger log = LoggerFactory.getLogger(TestServicesObject.class);

	@Context
	private HttpServletResponse response;

	private void setCORSHeaders() {
		// Allow cross-site - the test page is served from karma, so accessing wildfly is a cross-site request
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "origin, x-csrftoken, content-type, accept");
	}

	@OPTIONS
	@Path("/doubleUpBody")
	public String doubleUpBodyOptions() {
		setCORSHeaders();
		return "";
	}

	@POST
	@Path("/doubleUpBody")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse doubleUpBody(JsonRequest x) {
		log.info("Inside doubleUpBody, x = {}", x);
		setCORSHeaders();
		JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setDoubleA(x.getA() + x.getA());
		jsonResponse.setDoubleB(x.getB() + x.getB());
		jsonResponse.setDoubleBoth(x.getA() + x.getB() + x.getA() + x.getB());
		return jsonResponse;
	}


}

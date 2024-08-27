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

package com.bluecirclesoft.open.jigen.integrationJakartaee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

/**
 * Services that are invalid in some form or fashion
 */
@Path("/testServicesInvalid")
@Stateless
public class TestServicesInvalid {

	private static final Logger log = LoggerFactory.getLogger(TestServicesInvalid.class);

	@Context
	private HttpServletResponse response;

	private void setCORSHeaders() {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT");
	}

	/**
	 * WildFly complains "RESTEASY003065: Cannot consume content type".
	 */
	@POST
	@Path("/doubleUpPostF")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String doubleUpPostF(@FormParam("x") String x) {
		log.info("Inside doubleUpPostQ, x = {}", x);
		setCORSHeaders();
		return "\"" + x + x + "\"";
	}

}

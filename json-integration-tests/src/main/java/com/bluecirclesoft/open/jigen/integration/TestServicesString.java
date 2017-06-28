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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Services that return String
 */
@Path("/testServicesString")
@Stateless
public class TestServicesString {

	private static final Logger log = LoggerFactory.getLogger(TestServicesString.class);

	@Context
	private HttpServletResponse response;

	private void setCORSHeaders() {
		// Allow cross-site - the test page is served from karma, so accessing wildfly is a cross-site request
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT");
	}


	@GET
	@Path("/serviceCheck")
	@Produces(MediaType.TEXT_PLAIN)
	public String serviceCheck() {
		log.info("Service check invoked");
		return "Service check successful";
	}

	@GET
	@Path("/doubleUpGetQ")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String doubleUpGetQ(@QueryParam("x") String x) {
		log.info("Inside doubleUpGetQ, x = {}", x);
		setCORSHeaders();
		return "\"" + x + x + "\"";
	}

	@GET
	@Path("/doubleUpGetP/{x}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String doubleUpGetP(@PathParam("x") String x) {
		log.info("Inside doubleUpGetP, x = {}", x);
		setCORSHeaders();
		return "\"" + x + x + "\"";
	}

	@POST
	@Path("/doubleUpPostQ")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String doubleUpPostQ(@QueryParam("x") String x) {
		log.info("Inside doubleUpPostQ, x = {}", x);
		setCORSHeaders();
		return "\"" + x + x + "\"";
	}

	@POST
	@Path("/doubleUpPostF")
	@Produces(MediaType.APPLICATION_JSON)
	public String doubleUpPostF(@FormParam("x") String x) {
		log.info("Inside doubleUpPostQ, x = {}", x);
		setCORSHeaders();
		return "\"" + x + x + "\"";
	}

	@POST
	@Path("/doubleUpPostP/{x}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String doubleUpPostP(@PathParam("x") String x) {
		log.info("Inside doubleUpPostP, x = {}", x);
		setCORSHeaders();
		return "\"" + x + x + "\"";
	}

	@GET
	@Path("/doubleArrGetQ")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> doubleArrGetQ(@QueryParam("x") String x) {
		log.info("Inside doubleArrGetQ, x = {}", x);
		setCORSHeaders();
		return makeList(x, 3);
	}

	private List<String> makeList(String x, int i) {
		List<String> result = new ArrayList<>(i);
		for (int j = 0; j < i; j++) {
			result.add(x);
		}
		return result;
	}

	@GET
	@Path("/doubleArrGetP/{x}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> doubleArrGetP(@PathParam("x") String x) {
		log.info("Inside doubleArrGetP, x = {}", x);
		setCORSHeaders();
		return makeList(x, 3);
	}

	@POST
	@Path("/doubleArrPostQ")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> doubleArrPostQ(@QueryParam("x") String x) {
		log.info("Inside doubleArrPostQ, x = {}", x);
		setCORSHeaders();
		return makeList(x, 3);
	}

	@POST
	@Path("/doubleArrPostP/{x}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> doubleArrPostP(@PathParam("x") String x) {
		log.info("Inside doubleArrPostP, x = {}", x);
		setCORSHeaders();
		return makeList(x, 3);
	}

	@POST
	@Path("/doubleArrPostF")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> doubleArrPostF(@FormParam("x") String x) {
		log.info("Inside doubleArrPostF, x = {}", x);
		setCORSHeaders();
		return makeList(x, 3);
	}
}

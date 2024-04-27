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

import jakarta.ejb.Stateless;
import jakarta.enterprise.context.SessionScoped;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Services that return String
 */
@Path("/testServicesVoid")
@Stateless
public class TestServicesVoid {

	private static final Logger log = LoggerFactory.getLogger(TestServicesVoid.class);

	@SessionScoped
	private VoidTestState voidTestState;

	@Context
	private HttpServletResponse response;

	private void setCORSHeaders() {
		// Allow cross-site - the test page is served from karma, so accessing wildfly is a cross-site request
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT");
	}

	@GET
	@Path("/voidGetQ")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void voidGetQ(@QueryParam("x") String x) {
		log.info("Inside doubleUpGetQ, x = {}", x);
		setCORSHeaders();
		voidTestState.addToTotalString(x);
	}

	@GET
	@Path("/voidGetP/{x}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void doubleUpGetP(@PathParam("x") String x) {
		log.info("Inside doubleUpGetP, x = {}", x);
		setCORSHeaders();
		voidTestState.addToTotalString(x);
	}

	@POST
	@Path("/voidPostQ")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void doubleUpPostQ(@QueryParam("x") String x) {
		log.info("Inside doubleUpPostQ, x = {}", x);
		setCORSHeaders();
		voidTestState.addToTotalString(x);
	}

	@POST
	@Path("/voidPostP/{x}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void doubleUpPostP(@PathParam("x") String x) {
		log.info("Inside doubleUpPostP, x = {}", x);
		setCORSHeaders();
		voidTestState.addToTotalString(x);
	}

	@POST
	@Path("/voidPostF/{x}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void doubleUpPostF(@FormParam("x") String x) {
		log.info("Inside doubleUpPostP, x = {}", x);
		setCORSHeaders();
		voidTestState.addToTotalString(x);
	}

	@GET
	@Path("/getTotal")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTotal() {
		return '\"' + voidTestState.getTotalString() + '\"';
	}
}

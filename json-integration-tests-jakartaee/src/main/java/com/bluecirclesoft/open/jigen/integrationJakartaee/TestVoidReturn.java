/*
 * Copyright 2026 Blue Circle Software, LLC
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

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Path("/testVoidReturn")
@Slf4j
public class TestVoidReturn {

	@Context
	private HttpServletResponse response;

	private void setCORSHeaders() {
		// Allow cross-site - the test page is served from karma, so accessing wildfly is a cross-site request
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "origin, x-csrftoken, content-type, accept");
	}

	@OPTIONS
	@Path("/runTest")
	public String runTestOptions() {
		setCORSHeaders();
		return "";
	}

	@POST
	@Path("/runTest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void runTest(List<Integer> x) {
		// CORS headers must be set on the POST response, even for void returns.
		setCORSHeaders();
		log.info("I got {}", x);
	}
}

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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated test class to test all parameter types using two parameters (TODO except @FormParam!)
 */
@Path("/testAllCombosTwoParameters")
@Stateless
public class TestAllCombosTwoParameters {

	private static final Logger log = LoggerFactory.getLogger(TestAllCombosTwoParameters.class);

	@Context
	private HttpServletResponse response;

	private void setCORSHeaders() {
		// Allow cross-site - the test page is served from karma, so accessing wildfly is a cross-site request
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "origin, x-csrftoken, content-type, accept");
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeFoFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeFoFoOptions() {
		log.info("Called testAllCombosTwoParametersGeFoFoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeFoFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeFoFo(@FormParam("p0") String p0, @FormParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGePaFo/{p0}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGePaFoOptions(@PathParam("p0") String p0) {
		log.info("Called testAllCombosTwoParametersGePaFoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGePaFo/{p0}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGePaFo(@PathParam("p0") String p0, @FormParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeQuFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeQuFoOptions() {
		log.info("Called testAllCombosTwoParametersGeQuFoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeQuFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeQuFo(@QueryParam("p0") String p0, @FormParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeBoFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeBoFoOptions() {
		log.info("Called testAllCombosTwoParametersGeBoFoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeBoFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeBoFo(TestDto p0, @FormParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeFoPa/{p1}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeFoPaOptions(@PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoPaOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeFoPa/{p1}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeFoPa(@FormParam("p0") String p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGePaPa/{p0}/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGePaPaOptions(@PathParam("p0") String p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaPaOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGePaPa/{p0}/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGePaPa(@PathParam("p0") String p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeQuPa/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeQuPaOptions(@PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuPaOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeQuPa/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeQuPa(@QueryParam("p0") String p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeBoPa/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeBoPaOptions(@PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoPaOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeBoPa/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeBoPa(TestDto p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeFoQu")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeFoQuOptions() {
		log.info("Called testAllCombosTwoParametersGeFoQuOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeFoQu")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeFoQu(@FormParam("p0") String p0, @QueryParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGePaQu/{p0}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGePaQuOptions(@PathParam("p0") String p0) {
		log.info("Called testAllCombosTwoParametersGePaQuOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGePaQu/{p0}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGePaQu(@PathParam("p0") String p0, @QueryParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeQuQu")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeQuQuOptions() {
		log.info("Called testAllCombosTwoParametersGeQuQuOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeQuQu")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeQuQu(@QueryParam("p0") String p0, @QueryParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeBoQu")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeBoQuOptions() {
		log.info("Called testAllCombosTwoParametersGeBoQuOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeBoQu")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeBoQu(TestDto p0, @QueryParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeFoBo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeFoBoOptions() {
		log.info("Called testAllCombosTwoParametersGeFoBoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeFoBo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeFoBo(@FormParam("p0") String p0, TestDto p1) {
		log.info("Called testAllCombosTwoParametersGeFoBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGePaBo/{p0}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGePaBoOptions(@PathParam("p0") String p0) {
		log.info("Called testAllCombosTwoParametersGePaBoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGePaBo/{p0}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGePaBo(@PathParam("p0") String p0, TestDto p1) {
		log.info("Called testAllCombosTwoParametersGePaBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeQuBo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeQuBoOptions() {
		log.info("Called testAllCombosTwoParametersGeQuBoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeQuBo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeQuBo(@QueryParam("p0") String p0, TestDto p1) {
		log.info("Called testAllCombosTwoParametersGeQuBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersGeBoBo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersGeBoBoOptions() {
		log.info("Called testAllCombosTwoParametersGeBoBoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@GET
	@Path("/testAllCombosTwoParametersGeBoBo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersGeBoBo(TestDto p0, TestDto p1) {
		log.info("Called testAllCombosTwoParametersGeBoBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoFoFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoFoFoOptions() {
		log.info("Called testAllCombosTwoParametersPoFoFoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoFoFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoFoFo(@FormParam("p0") String p0, @FormParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoPaFo/{p0}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoPaFoOptions(@PathParam("p0") String p0) {
		log.info("Called testAllCombosTwoParametersPoPaFoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoPaFo/{p0}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoPaFo(@PathParam("p0") String p0, @FormParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoQuFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoQuFoOptions() {
		log.info("Called testAllCombosTwoParametersPoQuFoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoQuFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoQuFo(@QueryParam("p0") String p0, @FormParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoBoFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoBoFoOptions() {
		log.info("Called testAllCombosTwoParametersPoBoFoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoBoFo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoBoFo(TestDto p0, @FormParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoFoPa/{p1}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoFoPaOptions(@PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoPaOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoFoPa/{p1}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoFoPa(@FormParam("p0") String p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoPaPa/{p0}/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoPaPaOptions(@PathParam("p0") String p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaPaOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoPaPa/{p0}/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoPaPa(@PathParam("p0") String p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoQuPa/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoQuPaOptions(@PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuPaOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoQuPa/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoQuPa(@QueryParam("p0") String p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoBoPa/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoBoPaOptions(@PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoPaOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoBoPa/{p1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoBoPa(TestDto p0, @PathParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoFoQu")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoFoQuOptions() {
		log.info("Called testAllCombosTwoParametersPoFoQuOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoFoQu")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoFoQu(@FormParam("p0") String p0, @QueryParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoPaQu/{p0}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoPaQuOptions(@PathParam("p0") String p0) {
		log.info("Called testAllCombosTwoParametersPoPaQuOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoPaQu/{p0}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoPaQu(@PathParam("p0") String p0, @QueryParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoQuQu")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoQuQuOptions() {
		log.info("Called testAllCombosTwoParametersPoQuQuOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoQuQu")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoQuQu(@QueryParam("p0") String p0, @QueryParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoBoQu")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoBoQuOptions() {
		log.info("Called testAllCombosTwoParametersPoBoQuOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoBoQu")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoBoQu(TestDto p0, @QueryParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoFoBo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoFoBoOptions() {
		log.info("Called testAllCombosTwoParametersPoFoBoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoFoBo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoFoBo(@FormParam("p0") String p0, TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoFoBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoPaBo/{p0}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoPaBoOptions(@PathParam("p0") String p0) {
		log.info("Called testAllCombosTwoParametersPoPaBoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoPaBo/{p0}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoPaBo(@PathParam("p0") String p0, TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoPaBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoQuBo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoQuBoOptions() {
		log.info("Called testAllCombosTwoParametersPoQuBoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoQuBo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoQuBo(@QueryParam("p0") String p0, TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoQuBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@OPTIONS
	@Path("/testAllCombosTwoParametersPoBoBo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAllCombosTwoParametersPoBoBoOptions() {
		log.info("Called testAllCombosTwoParametersPoBoBoOptions");
		setCORSHeaders();
		return Response.ok().build();
	}

	@POST
	@Path("/testAllCombosTwoParametersPoBoBo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestDto testAllCombosTwoParametersPoBoBo(TestDto p0, TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoBoBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

}

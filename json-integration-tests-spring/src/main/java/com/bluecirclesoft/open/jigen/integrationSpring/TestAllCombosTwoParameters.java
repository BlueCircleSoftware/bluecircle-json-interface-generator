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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/testAllCombosTwoParameters")
@Component
public class TestAllCombosTwoParameters {

	private static final Logger log = LoggerFactory.getLogger(TestAllCombosTwoParameters.class);

	@Autowired
	private HttpServletResponse response;

	private void setCORSHeaders() {
		// Allow cross-site - the test page is served from karma, so accessing wildfly is a cross-site request
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "origin, x-csrftoken, content-type, accept");
	}

	@RequestMapping(method = RequestMethod.OPTIONS,
			path = "/**",
			consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> testAllOptions() {
		setCORSHeaders();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeFoFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeFoFo(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGePaFo/{p0}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGePaFo(@PathVariable("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeQuFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeQuFo(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeBoFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeBoFo(@RequestBody TestDto p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeFoPa/{p1}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeFoPa(@RequestParam("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGePaPa/{p0}/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGePaPa(@PathVariable("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeQuPa/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeQuPa(@RequestParam("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeBoPa/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeBoPa(@RequestBody TestDto p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeFoQu",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeFoQu(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGePaQu/{p0}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGePaQu(@PathVariable("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeQuQu",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeQuQu(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeBoQu",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeBoQu(@RequestBody TestDto p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeFoBo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeFoBo(@RequestParam("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersGeFoBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGePaBo/{p0}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGePaBo(@PathVariable("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersGePaBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeQuBo",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeQuBo(@RequestParam("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersGeQuBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeBoBo",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersGeBoBo(@RequestBody TestDto p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersGeBoBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoFoFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoFoFo(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoPaFo/{p0}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoPaFo(@PathVariable("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoQuFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoQuFo(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoBoFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoBoFo(@RequestBody TestDto p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoFo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoFoPa/{p1}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoFoPa(@RequestParam("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoPaPa/{p0}/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoPaPa(@PathVariable("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoQuPa/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoQuPa(@RequestParam("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoBoPa/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoBoPa(@RequestBody TestDto p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoPa");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoFoQu",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoFoQu(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoPaQu/{p0}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoPaQu(@PathVariable("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoQuQu",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoQuQu(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoBoQu",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoBoQu(@RequestBody TestDto p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoQu");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoFoBo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoFoBo(@RequestParam("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoFoBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoPaBo/{p0}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoPaBo(@PathVariable("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoPaBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoQuBo",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoQuBo(@RequestParam("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoQuBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoBoBo",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TestDto testAllCombosTwoParametersPoBoBo(@RequestBody TestDto p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoBoBo");
		setCORSHeaders();
		return MergeHelper.merge(p0, p1);
	}

}
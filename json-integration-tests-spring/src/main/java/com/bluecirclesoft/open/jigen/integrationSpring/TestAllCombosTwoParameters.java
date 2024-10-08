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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/testAllCombosTwoParameters")
@CrossOrigin
@Component
public class TestAllCombosTwoParameters {

	private static final Logger log = LoggerFactory.getLogger(TestAllCombosTwoParameters.class);

	@Autowired
	private HttpServletResponse response;

	@RequestMapping(method = RequestMethod.GET, path = "/testAllCombosTwoParametersGeFoFo", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeFoFo(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoFo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGePaFo/{p0}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGePaFo(@PathVariable("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaFo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeQuFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeQuFo(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuFo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeBoFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeBoFo(@RequestBody TestDto p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoFo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeFoPa/{p1}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeFoPa(@RequestParam("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoPa");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGePaPa/{p0}/{p1}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGePaPa(@PathVariable("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaPa");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeQuPa/{p1}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeQuPa(@RequestParam("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuPa");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeBoPa/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeBoPa(@RequestBody TestDto p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoPa");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeFoQu",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeFoQu(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeFoQu");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGePaQu/{p0}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGePaQu(@PathVariable("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGePaQu");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeQuQu",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeQuQu(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeQuQu");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeBoQu",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeBoQu(@RequestBody TestDto p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersGeBoQu");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeFoBo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeFoBo(@RequestParam("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersGeFoBo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGePaBo/{p0}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGePaBo(@PathVariable("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersGePaBo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeQuBo",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeQuBo(@RequestParam("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersGeQuBo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.GET,
			path = "/testAllCombosTwoParametersGeBoBo",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersGeBoBo(@RequestBody TestDto p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersGeBoBo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoFoFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoFoFo(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoFo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoPaFo/{p0}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoPaFo(@PathVariable("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaFo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoQuFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoQuFo(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuFo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoBoFo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoBoFo(@RequestBody TestDto p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoFo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoFoPa/{p1}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoFoPa(@RequestParam("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoPa");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoPaPa/{p0}/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoPaPa(@PathVariable("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaPa");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoQuPa/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoQuPa(@RequestParam("p0") String p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuPa");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoBoPa/{p1}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoBoPa(@RequestBody TestDto p0, @PathVariable("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoPa");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoFoQu",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoFoQu(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoFoQu");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoPaQu/{p0}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoPaQu(@PathVariable("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoPaQu");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoQuQu",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoQuQu(@RequestParam("p0") String p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoQuQu");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoBoQu",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoBoQu(@RequestBody TestDto p0, @RequestParam("p1") String p1) {
		log.info("Called testAllCombosTwoParametersPoBoQu");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoFoBo",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoFoBo(@RequestParam("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoFoBo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoPaBo/{p0}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoPaBo(@PathVariable("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoPaBo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoQuBo",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoQuBo(@RequestParam("p0") String p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoQuBo");
		return MergeHelper.merge(p0, p1);
	}

	@RequestMapping(method = RequestMethod.POST,
			path = "/testAllCombosTwoParametersPoBoBo",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestDto testAllCombosTwoParametersPoBoBo(@RequestBody TestDto p0, @RequestBody TestDto p1) {
		log.info("Called testAllCombosTwoParametersPoBoBo");
		return MergeHelper.merge(p0, p1);
	}

}
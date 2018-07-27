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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bluecirclesoft.open.jigen.integrationSpring.testPackage1.GenericList;
import com.bluecirclesoft.open.jigen.integrationSpring.testPackage1.p11.ClassA;
import com.bluecirclesoft.open.jigen.integrationSpring.testPackage1.p12.ClassB;
import com.bluecirclesoft.open.jigen.integrationSpring.testPackage1.p12.ClassC;
import com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.EnumA;

/**
 * Test services that consume/produce objects
 */
@RequestMapping("/testServicesObject")
@Component
public class TestServicesObject {

	private static final Logger log = LoggerFactory.getLogger(TestServicesObject.class);

	@Autowired
	private HttpServletResponse response;

	private void setCORSHeaders() {
		// Allow cross-site - the test page is served from karma, so accessing wildfly is a cross-site request
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "origin, x-csrftoken, content-type, accept");
	}

	@RequestMapping(path = "/doubleUpBody", method = RequestMethod.OPTIONS)
	public String doubleUpBodyOptions() {
		setCORSHeaders();
		return "";
	}

	@PostMapping(path = "/doubleUpBody", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	JsonResponse doubleUpBody(@RequestBody JsonRequest x) {
		log.info("Inside doubleUpBody, x = {}", x);
		setCORSHeaders();
		JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setDoubleA(x.getA() + x.getA());
		jsonResponse.setDoubleB(x.getB() + x.getB());
		jsonResponse.setDoubleBoth(x.getA() + x.getB() + x.getA() + x.getB());
		return jsonResponse;
	}

	@RequestMapping(method = RequestMethod.OPTIONS, path = "/doubleUpNested")
	public @ResponseBody
	List<String> doubleUpNestedOptions() {
		setCORSHeaders();
		return Collections.singletonList("");
	}

	@PostMapping(path = "/doubleUpNested", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	NestedOuter doubleUpNested(@RequestBody NestedOuter x) {
		log.info("Inside doubleUpNested, x = {}", x);
		setCORSHeaders();
		NestedOuter response = new NestedOuter();
		response.setA(x.getA());
		response.setB(x.getB());
		response.setC(x.getC() * 2);
		response.setD(x.getD() + x.getD());
		return response;
	}

	@GetMapping(path = "/getClassC", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	ClassC getClassC() {
		log.info("Inside getClassC");
		setCORSHeaders();
		ClassC response = new ClassC();
		response.setB(new ClassB());
		response.getB().setA(new ClassA());
		return response;
	}

	@GetMapping(path = "/getClassB", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassB getClassB() {
		log.info("Inside getClassB");
		setCORSHeaders();

		com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassA a1 =
				new com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassA();
		a1.setProp1(1);
		a1.setProp2("Str");
		a1.setProp3(new ArrayList<>());
		Collections.addAll(a1.getProp3(), "e1", "e2", "e3", "e4");
		a1.setProp4(EnumA.THREE);
		a1.setProp5(new ArrayList<>());
		Collections.addAll(a1.getProp5(), EnumA.TWO, EnumA.ONE, EnumA.TWO);
		a1.getProp5().add(EnumA.TWO);
		a1.getProp5().add(EnumA.ONE);
		a1.setProp6(new HashSet<>());
		Collections.addAll(a1.getProp6(), 10, 15, 12, 17, 1);
		a1.setProp7(new HashMap<>());
		a1.getProp7().put("k1", "v1");
		a1.getProp7().put("k2", "v2");
		a1.setProp8(new HashMap<>());
		a1.getProp8().put(1, "Wun");
		a1.getProp8().put(2, "Too");
		a1.getProp8().put(3, "Three");
		Map<String, String> k1 = new HashMap<>();
		k1.put("a", "b");
		Map<Integer, Integer> v1 = new HashMap<>();
		v1.put(1, 2);
		a1.getProp9().put(k1, v1);

		com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassB b =
				new com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassB();
		b.setOne(a1);
		b.setTwo(new com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassA());
		b.setThree(new com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassC<>(34, "blah"));
		b.setFour(new com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassC<>(127, 128));
		b.setFive(new com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassA[]{
				new com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassA(),
				new com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassA(),
				new com.bluecirclesoft.open.jigen.integrationSpring.testPackage2.ClassA()});

		return b;
	}

	@GetMapping(path = "/getGenericListOfInt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	GenericList<Integer> getGenericListOfInt() {
		log.info("Inside getGenericListOfInt");
		GenericList<Integer> result = new GenericList<>();
		List<Integer> l = new ArrayList<>();
		l.add(1);
		l.add(3);
		l.add(5);
		result.setList(l);
		return result;
	}
}

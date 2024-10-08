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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage1.GenericList;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage1.p11.ClassA;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage1.p12.ClassB;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage1.p12.ClassC;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.EnumA;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage3.Sub1;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage3.Sub2;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage3.Sub3;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage3.Super;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage4.TP4Super;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage4.TP4Super2;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage5.TP5Weird;
import com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage5.TP5Weird2;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

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

	@OPTIONS
	@Path("/doubleUpNested")
	public String doubleUpNestedOptions() {
		setCORSHeaders();
		return "";
	}

	@POST
	@Path("/doubleUpNested")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public NestedOuter doubleUpNested(NestedOuter x) {
		log.info("Inside doubleUpNested, x = {}", x);
		setCORSHeaders();
		NestedOuter response = new NestedOuter();
		response.setA(x.getA());
		response.setB(x.getB());
		response.setC(x.getC() * 2);
		response.setD(x.getD() + x.getD());
		return response;
	}

	@GET
	@Path("/getClassC")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClassC getClassC() {
		log.info("Inside getClassC");
		setCORSHeaders();
		ClassC response = new ClassC();
		response.setB(new ClassB());
		response.getB().setA(new ClassA());
		return response;
	}

	@GET
	@Path("/getClassB")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassB getClassB() {
		log.info("Inside getClassB");
		setCORSHeaders();

		com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassA a1 =
				new com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassA();
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

		com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassB b =
				new com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassB();
		b.setOne(a1);
		b.setTwo(new com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassA());
		b.setThree(new com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassC<>(34, "blah"));
		b.setFour(new com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassC<>(127, 128));
		b.setFive(new com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassA[]{
				new com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassA(),
				new com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassA(),
				new com.bluecirclesoft.open.jigen.integrationJakartaee.testPackage2.ClassA()});

		return b;
	}

	@GET
	@Path("/getGenericListOfInt")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericList<Integer> getGenericListOfInt() {
		GenericList<Integer> result = new GenericList<>();
		List<Integer> l = new ArrayList<>();
		l.add(1);
		l.add(3);
		l.add(5);
		result.setList(l);
		return result;
	}

	@GET
	@Path("/getGenericListOfAny")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericList<?> getGenericListOfAny() {
		GenericList<Integer> result = new GenericList<>();
		List<Integer> l = new ArrayList<>();
		l.add(1);
		l.add(3);
		l.add(5);
		result.setList(l);
		return result;
	}

	@GET
	@Path("/getGenericListOfAnyNumber")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericList<? extends Number> getGenericListOfAnyNumber() {
		GenericList<Integer> result = new GenericList<>();
		List<Integer> l = new ArrayList<>();
		l.add(1);
		l.add(3);
		l.add(5);
		result.setList(l);
		return result;
	}

	@OPTIONS
	@Path("/getGenericListOfSupers")
	public String getGenericListSupersOptions() {
		setCORSHeaders();
		return "";
	}

	@GET
	@Path("/getGenericListOfSupers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericList<Super> getGenericListSupers() {
		log.info("Inside getGenericListSupers");
		setCORSHeaders();
		GenericList<Super> result = new GenericList<>();
		List<Super> l = new ArrayList<>();
		l.add(new Sub1());
		l.add(new Sub2());
		l.add(new Sub3());
		result.setList(l);
		return result;
	}

	@GET
	@Path("/getTP5Weird")
	@Produces(MediaType.APPLICATION_JSON)
	public TP5Weird<TP4Super> getTP5Weird() {
		return new TP5Weird<>();
	}

	@GET
	@Path("/getTP5Weird2")
	@Produces(MediaType.APPLICATION_JSON)
	public TP5Weird2<TP4Super2> getTP5Weird2() {
		return new TP5Weird2<>();
	}
}

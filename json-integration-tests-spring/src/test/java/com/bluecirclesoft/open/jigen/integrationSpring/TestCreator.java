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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.output.CommaSep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Create integration tests: creates both the JAX-RS service, and the test class to exercise the service.
 */
public class TestCreator {

	private static final Logger log = LoggerFactory.getLogger(TestCreator.class);

	private enum HttpMethod {
		GET("Ge"),
		POST("Po");

		final String acronym;

		HttpMethod(String acronym) {
			this.acronym = acronym;
		}
	}

	private enum ParamType {
		FORM(false, "Fo"),
		PATH(false, "Pa"),
		QUERY(false, "Qu"),
		BODY(true, "Bo");

		final boolean canBeObject;

		final String acronym;

		ParamType(boolean canBeObject, String acronym) {
			this.canBeObject = canBeObject;
			this.acronym = acronym;
		}
	}

	public static void main(String[] args) {
		generate(2, "testAllCombosTwoParameters");
	}


	public static void generate(int pCount, String testName) {
		StringBuilder serviceClass = new StringBuilder();
		StringBuilder testClass = new StringBuilder();
		String serviceName = Character.toUpperCase(testName.charAt(0)) + testName.substring(1);
		String methodPrefix = Character.toLowerCase(testName.charAt(0)) + testName.substring(1);
		String servicePath = "/" + methodPrefix;

		createTestHeader(serviceName, testClass);

		createServiceHeader(serviceClass, serviceName, methodPrefix);

		TestDto testParam = new TestDto();
		testParam.setA("abc");
		testParam.setB("def");
		testParam.setC("ghi");

		for (HttpMethod httpMethod : HttpMethod.values()) {
			int[] params = new int[pCount];
			do {
				StringBuilder suffix = new StringBuilder(httpMethod.acronym);
				for (int param : params) {
					ParamType paramType = ParamType.values()[param];
					suffix.append(paramType.acronym);
				}
				String testMethod = methodPrefix + suffix;

				// create service
				createServiceMethod(serviceClass, httpMethod, params, testMethod);

				createTestMethod(params, testClass, testParam, serviceName, testMethod);

			} while (next(params));
		}

		createTestFooter(testClass);

		log.info("Service class:\n{}", serviceClass.toString());
		log.info("Test class:\n{}", testClass.toString());
	}

	private static void createServiceHeader(StringBuilder serviceClass, String serviceName, String methodPrefix) {
		serviceClass.append("@RequestMapping(\"/" + methodPrefix + "\")\n");
		serviceClass.append("@Component\n");
		serviceClass.append("public class " + serviceName + " {\n");
		serviceClass.append("\tprivate static final Logger log = LoggerFactory.getLogger(" + serviceName + ".class);\n");
		serviceClass.append("\n");
		serviceClass.append("\t@Autowired\n");
		serviceClass.append("\tprivate HttpServletResponse response;\n");
		serviceClass.append("\n");
		serviceClass.append("\tprivate void setCORSHeaders() {\n");
		serviceClass.append("\t\t// Allow cross-site - the test page is served from karma, so accessing wildfly is a cross-site request\n");
		serviceClass.append("\t\tresponse.setHeader(\"Access-Control-Allow-Origin\", \"*\");\n");
		serviceClass.append("\t\tresponse.setHeader(\"Access-Control-Allow-Methods\", \"GET, POST, OPTIONS\");\n");
		serviceClass.append("\t\tresponse.setHeader(\"Access-Control-Allow-Headers\", \"origin, x-csrftoken, content-type, accept\");\n");
		serviceClass.append("\t}\n");
	}

	private static void createTestFooter(StringBuilder testClass) {
		testClass.append("\n");
		testClass.append("});\n");
	}

	private static void createTestMethod(int[] params, StringBuilder testClass, TestDto paramObject, String serviceName,
	                                     String methodName) {
		testClass.append("\n");
		testClass.append("    it(\"can execute ").append(methodName).append("\", () => {\n");
		testClass.append("        let result: object = {};\n");
		testClass.append("        com.bluecirclesoft.open.jigen.integration.")
				.append(serviceName)
				.append(".")
				.append(methodName)
				.append("(");
		int pCount = params.length;
		String paramJson = jsonify(paramObject);
		TestDto resultObject = new TestDto();
		for (int i = 0; i < pCount; i++) {
			ParamType paramType = ParamType.values()[params[i]];
			if (paramType.canBeObject) {
				testClass.append(paramJson);
				resultObject.append(paramObject);
			} else {
				String pVal = "p" + i;
				testClass.append("'" + pVal + "'");
				resultObject.appendAll(pVal);
			}
			testClass.append(", ");
		}
		testClass.append("simpleHandler((s: object) => {\n");
		testClass.append("            result = s;\n");
		testClass.append("        }));\n");
		testClass.append("        expect(result).toEqual(").append(jsonify(resultObject)).append(");\n");
		testClass.append("    });\n");
	}

	private static String jsonify(TestDto resultObject) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(resultObject);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private static void createTestHeader(String testName, StringBuilder testClass) {
		testClass.append("describe(\"test ").append(testName).append("\", () => {\n");
		testClass.append("\n");
		testClass.append("    // Standard handler\n");
		testClass.append("    function simpleHandler<T>(handler: (x: T) => void): JsonOptions<T> {\n");
		testClass.append("        return {\n");
		testClass.append("            success: (s: T) => {\n");
		testClass.append("                console.log(\"success: result \", s);\n");
		testClass.append("                handler(s);\n");
		testClass.append("            },\n");
		testClass.append("            error: (errorThrown: string) => {\n");
		testClass.append("                console.log(\"errorThrown=\", errorThrown);\n");
		testClass.append("            },\n");
		testClass.append("            complete: () => {\n");
		testClass.append("                console.log(\"complete\");\n");
		testClass.append("            },\n");
		testClass.append("            async: false\n");
		testClass.append("        };\n");
		testClass.append("    }\n");
	}

	private static void createServiceMethod(StringBuilder serviceClass, HttpMethod httpMethod, int[] params, String testMethod) {
		// create JAX-RS path
		StringBuilder path = new StringBuilder();
		path.append("/").append(testMethod);

		boolean hasFormParam = false;
		formSearch:
		for (int param : params) {
			ParamType paramType = ParamType.values()[param];
			switch (paramType) {
				case FORM:
					hasFormParam = true;
					break formSearch;
			}
		}

		for (int paramNum = 0; paramNum < params.length; paramNum++) {
			int param = params[paramNum];
			ParamType paramType = ParamType.values()[param];
			switch (paramType) {
				case PATH:
					path.append("/{p").append(paramNum).append("}");
					break;
			}
		}
		String plist = makeServicesParameterList(params);
		serviceClass.append("\n");
		serviceClass.append("\t@RequestMapping(method = RequestMethod.OPTIONS, path = ");
		serviceClass.append("\"" + path.toString() + "\", ");
		if (hasFormParam) {
			serviceClass.append("consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, ");
		} else {
			serviceClass.append("consumes = MediaType.APPLICATION_JSON_VALUE, ");
		}
		serviceClass.append("produces = MediaType.APPLICATION_JSON_VALUE)\n");
		serviceClass.append(
				"\tpublic ResponseEntity<Void> " + testMethod + "Options(" + makeServicesOptionsParameterList(params) + ") " + "{\n");
		serviceClass.append("\t\tlog.info(\"Called " + testMethod + "Options\");\n");
		serviceClass.append("\t\tsetCORSHeaders();\n");
		serviceClass.append("\t\treturn new ResponseEntity<>(HttpStatus.OK);\n");
		serviceClass.append("\t}\n");
		serviceClass.append("\n");
		serviceClass.append("\t@RequestMapping(method = RequestMethod.").append(httpMethod.name()).append(", ");
		serviceClass.append("path = \"").append(path.toString()).append("\"");
		if (hasFormParam) {
			serviceClass.append(", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE");
		} else {
			serviceClass.append(", consumes = MediaType.APPLICATION_JSON_VALUE");
		}
		serviceClass.append(", produces = MediaType.APPLICATION_JSON_VALUE)\n");
		serviceClass.append("\tpublic TestDto ").append(testMethod).append("(");
		serviceClass.append(plist);
		serviceClass.append(") {\n");
		serviceClass.append("\t\tlog.info(\"Called " + testMethod + "\");\n");
		serviceClass.append("\t\tsetCORSHeaders();\n");
		serviceClass.append("\t\treturn MergeHelper.merge(");
		boolean needsComma = false;
		for (int paramNum = 0; paramNum < params.length; paramNum++) {
			if (needsComma) {
				serviceClass.append(",");
			} else {
				needsComma = true;
			}
			serviceClass.append("p").append(paramNum);
		}
		serviceClass.append(");\n");
		serviceClass.append("\t}\n");
	}

	private static String makeServicesParameterList(int[] params) {
		CommaSep comma = new CommaSep();
		for (int paramNum = 0; paramNum < params.length; paramNum++) {
			int param = params[paramNum];
			ParamType paramType = ParamType.values()[param];
			String p = "";
			switch (paramType) {
				case BODY:
					p = "@RequestBody ";
					break;
				case FORM:
					p = "@RequestParam(\"p" + paramNum + "\") ";
					break;
				case PATH:
					p = "@PathVariable(\"p" + paramNum + "\") ";
					break;
				case QUERY:
					p = "@RequestParam(\"p" + paramNum + "\") ";
					break;
				default:
					throw new RuntimeException("Unhandled parameter type " + paramType);
			}
			if (paramType.canBeObject) {
				p += "TestDto p" + paramNum;
			} else {
				p += "String p" + paramNum;
			}
			comma.add(p);
		}
		return comma.get();
	}

	private static String makeServicesOptionsParameterList(int[] params) {
		CommaSep comma = new CommaSep();
		for (int paramNum = 0; paramNum < params.length; paramNum++) {
			int param = params[paramNum];
			ParamType paramType = ParamType.values()[param];
			switch (paramType) {
				case BODY:
				case QUERY:
				case FORM:
					break;
				case PATH:
					comma.add("@PathVariable(\"p" + paramNum + "\") String p" + paramNum);
					break;
				default:
					throw new RuntimeException("Unhandled parameter type " + paramType);
			}
		}
		return comma.get();
	}

	private static boolean next(int[] params) {
		int i = 0;
		params[i]++;
		int numParamTypes = ParamType.values().length;
		int numParams = params.length;
		while (params[i] >= numParamTypes) {
			if (i == numParams - 1) {
				return false;
			} else {
				params[i] = 0;
				i++;
				params[i]++;
			}
		}
		isWithinBounds(params);
		return true;
	}

	private static boolean isWithinBounds(int[] params) {
		for (int i = 0; i < params.length; i++) {
			int p = params[i];
			if (p >= ParamType.values().length) {
				throw new AssertionError("param type out of bounds: index " + i + ", value " + p);
			}
		}
		return true;
	}

	@Test
	public void testNext() {
		int[] i1 = new int[1];
		StringBuilder sb = new StringBuilder();
		do {
			sb.append(i1[0]).append("|");
		} while (next(i1));
		Assert.assertEquals("0|1|2|3|", sb.toString());

		sb = new StringBuilder();
		int[] i2 = new int[2];
		do {
			sb.append(i2[0]).append(i2[1]).append("|");
		} while (next(i2));
		Assert.assertEquals("00|10|20|30|01|11|21|31|02|12|22|32|03|13|23|33|", sb.toString());
	}

}

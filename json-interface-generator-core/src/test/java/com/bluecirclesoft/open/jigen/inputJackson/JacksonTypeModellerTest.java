/*
 * Copyright 2015 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.inputJackson;

import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.output.typeScript.TypeScriptProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO document me
 */
public class JacksonTypeModellerTest {

	private static final Logger log = LoggerFactory.getLogger(JacksonTypeModellerTest.class);

	@Test
	public void testConcreteJsonSchemaClasses() {
		Reflections reflections = new Reflections("com.fasterxml.jackson");
		Set<Class<? extends JsonSchema>> xclasses = reflections.getSubTypesOf(JsonSchema.class);
		Set<Class<?>> leafClasses = new HashSet<>(xclasses);
		// remove classes that are intermediate (non-leaf) classes
		for (Class<? extends JsonSchema> cl : xclasses) {
			leafClasses.remove(cl.getSuperclass());
		}
		Class<SchemaDispatcher> dispatcherClass = SchemaDispatcher.class;
		Set<Class<?>> handledClasses = new HashSet<>();
		for (Method method : dispatcherClass.getDeclaredMethods()) {
			if (method.getName().equals("handle")) {
				handledClasses.add(method.getParameterTypes()[0]);
			}
		}
		for (Class<?> cl : handledClasses) {
			if (!leafClasses.contains(cl)) {
				throw new RuntimeException("There is a handle() method for class " + cl.getName() +
						", but it is not a leaf descendent of JsonSchema");
			}
		}
		for (Class<?> cl : leafClasses) {
			if (!handledClasses.contains(cl)) {
				throw new RuntimeException(
						"There is no handle() method for the lead class " + cl.getName());
			}
		}
	}

	@Test
	public void test1() throws IOException {

		ClassA a1 = new ClassA();
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

		ClassB b = new ClassB();
		b.setOne(a1);
		b.setTwo(new ClassA());
		b.setThree(new ClassC<>(34, "blah"));
		b.setFour(new ClassC<>(127, 128));

		ObjectMapper om = new ObjectMapper();
		log.info("Sample serialized object: {}", om.writeValueAsString(b));

		JacksonTypeModeller jacksonTypeModeller = new JacksonTypeModeller();
		Model model = new Model();
		jacksonTypeModeller.enumerateProperties(model, ClassA.class, ClassB.class);
		log.info("Resulting model: {}", model);

		TypeScriptProducer outputTypeScript = new TypeScriptProducer(new PrintWriter(System.out));
		outputTypeScript.output(model);
	}

}

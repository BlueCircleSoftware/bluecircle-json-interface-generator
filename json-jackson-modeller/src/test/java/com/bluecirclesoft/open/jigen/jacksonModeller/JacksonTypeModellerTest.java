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
 */

package com.bluecirclesoft.open.jigen.jacksonModeller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bluecirclesoft.open.jigen.ClassOverrideHandler;
import com.bluecirclesoft.open.jigen.model.JBoolean;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JNumber;
import com.bluecirclesoft.open.jigen.model.JObject;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.model.SourcedType;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTypeModellerTest {

	private static final class BooleanValue {
		@JsonValue
		public boolean value() {
			return true;
		}
	}

	private static final class NumberValue {
		@JsonValue
		public BigDecimal value() {
			return BigDecimal.ONE;
		}
	}

	private enum DuplicateEnum {
		A("x"),
		B("x"),
		C("y");

		private final String value;

		DuplicateEnum(String value) {
			this.value = value;
		}

		@JsonValue
		public String value() {
			return value;
		}
	}

	private static final class NonPojoHolder {
		public NonPojoHolder() {
		}
	}

	@Test
	public void jsonValueBooleanUsesBooleanType() {
		JacksonTypeModeller modeller = newModeller();
		Model model = new Model();
		JType type = modeller.readOneType(model, new SourcedType(BooleanValue.class, "root", null));
		assertTrue(type instanceof JBoolean);
	}

	@Test
	public void jsonValueNumberUsesNumberType() {
		JacksonTypeModeller modeller = newModeller();
		Model model = new Model();
		JType type = modeller.readOneType(model, new SourcedType(NumberValue.class, "root", null));
		assertTrue(type instanceof JNumber);
	}

	@Test
	public void duplicateEnumValuesKeepFirst() throws Exception {
		JacksonTypeModeller modeller = newModeller();
		Model model = new Model();
		JType type = modeller.readOneType(model, new SourcedType(DuplicateEnum.class, "root", null));
		assertTrue(type instanceof JEnum);
		JEnum enumType = (JEnum) type;
		List<JEnum.EnumDeclaration> values = enumType.getValues();
		Set<String> serializedValues = new HashSet<>();
		Set<String> names = new HashSet<>();
		for (JEnum.EnumDeclaration decl : values) {
			serializedValues.add(getField(decl, "serializedValue"));
			names.add(getField(decl, "name"));
		}
		assertEquals(serializedValues.size(), values.size());
		assertFalse(names.contains("B"));
	}

	@Test
	public void nonPojoPropertyCallbacksDoNotThrow() {
		JacksonTypeModeller modeller = newModeller();
		SourcedType parent = new SourcedType(NonPojoHolder.class, "root", null);
		JsonObjectReader reader = new JsonObjectReader(modeller, NonPojoHolder.class, parent);
		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructType(new TypeReference<List<String>>() {
		});
		reader.property("values", null, javaType);
		reader.optionalProperty("maybe", null, javaType);

		JObject result = reader.getResult();
		Set<String> names = new HashSet<>();
		for (Map.Entry<String, JObject.Field> entry : result.getFieldEntries()) {
			names.add(entry.getKey());
		}
		assertTrue(names.contains("values"));
		assertTrue(names.contains("maybe"));
	}

	private static JacksonTypeModeller newModeller() {
		return new JacksonTypeModeller(new ClassOverrideHandler(), JEnum.EnumType.STRING, IncludeSubclasses.EXCLUDE,
				new String[] { JacksonTypeModellerTest.class.getPackage().getName() });
	}

	private static String getField(JEnum.EnumDeclaration decl, String fieldName) throws Exception {
		Field field = JEnum.EnumDeclaration.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		return (String) field.get(decl);
	}
}

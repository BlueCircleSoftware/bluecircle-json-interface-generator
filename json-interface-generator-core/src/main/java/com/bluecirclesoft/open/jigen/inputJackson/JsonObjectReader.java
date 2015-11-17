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

import com.bluecirclesoft.open.jigen.model.JObject;
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * TODO document me
 */
class JsonObjectReader extends JsonObjectFormatVisitor.Base implements TypeReadingVisitor<JObject> {

	private final JacksonTypeModeller jacksonTypeModeller;

	private final JObject jObject;

	JsonObjectReader(JacksonTypeModeller jacksonTypeModeller, Class<?> clazz) {
		this.jacksonTypeModeller = jacksonTypeModeller;
		jObject = new JObject(clazz.getName());
		for (int i = 0; i < clazz.getTypeParameters().length; i++) {
			final int finalI = i;
			jObject.getTypeVariables().add(null);
			jacksonTypeModeller.queueType(clazz.getTypeParameters()[i]);
			jacksonTypeModeller.addFixup(clazz.getTypeParameters()[i],
					jType -> jObject.getTypeVariables().set(finalI, (JTypeVariable) jType));
		}
	}

	private void handleField(BeanProperty prop) {
		String name = prop.getName();
		Type type;
		AnnotatedElement annotatedThing = prop.getMember().getAnnotated();
		if (annotatedThing instanceof Field) {
			type = ((Field) annotatedThing).getGenericType();
		} else if (annotatedThing instanceof Method) {
			type = ((Method) annotatedThing).getGenericReturnType();
		} else {
			throw new RuntimeException("Can't handle " + annotatedThing);
		}
		boolean required;
		if (type instanceof Class && ((Class) type).isPrimitive()) {
			required = true;
		} else {
			required = prop.isRequired();
		}
		jObject.declareProperty(name);
		jacksonTypeModeller.addFixup(type, jType -> jObject.makeProperty(name, jType, required));
		jacksonTypeModeller.queueType(type);
	}

	@Override
	public void property(BeanProperty prop) throws JsonMappingException {
		handleField(prop);

	}

	@Override
	public void property(String name, JsonFormatVisitable handler, JavaType propertyTypeHint)
			throws JsonMappingException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void optionalProperty(BeanProperty prop) throws JsonMappingException {
		handleField(prop);

	}

	@Override
	public void optionalProperty(String name, JsonFormatVisitable handler,
	                             JavaType propertyTypeHint) throws JsonMappingException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public JObject getResult() {
		return jObject;
	}
}

/*
 * Copyright 2018 Blue Circle Software, LLC
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.annotations.DiscriminatedBy;
import com.bluecirclesoft.open.jigen.annotations.TypeDiscriminator;
import com.bluecirclesoft.open.jigen.model.JObject;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.bluecirclesoft.open.jigen.model.SourcedType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;

/**
 * Use jackson to read a class definition, and create a TypeScript type definition as a result.
 */
class JsonObjectReader extends JsonObjectFormatVisitor.Base implements TypeReadingVisitor<JObject> {

	private static final Logger logger = LoggerFactory.getLogger(JsonObjectReader.class);

	private final JacksonTypeModeller jacksonTypeModeller;

	private final JObject jObject;

	private final SourcedType parent;

	/**
	 * Prepare to read a class and determine the resulting object properties
	 *
	 * @param jacksonTypeModeller the type modeller that contains the total model
	 * @param clazz               the class to read
	 */
	JsonObjectReader(JacksonTypeModeller jacksonTypeModeller, Class<?> clazz, SourcedType parent) {
		logger.debug("Using Jackson to read class {}", clazz.getName());
		this.parent = parent;
		this.jacksonTypeModeller = jacksonTypeModeller;
		// make a stub type for this class
		jObject = new JObject(clazz.getName(), clazz);
		// create a new instance, if possible
		jObject.setNewObjectJson(createEmptyJsonFor(clazz, parent));
		// read through the type parameters, and see if we need to queue up any other classes for reading
		for (int i = 0; i < clazz.getTypeParameters().length; i++) {
			final int finalI = i;
			jObject.getTypeVariables().add(null);
			jacksonTypeModeller.queueType(
					new SourcedType(clazz.getTypeParameters()[i], "Type parameter " + (i + 1) + " of " + clazz, parent));
			jacksonTypeModeller.addFixup(clazz.getTypeParameters()[i],
					jType -> jObject.getTypeVariables().set(finalI, (JTypeVariable) jType));
		}
		jacksonTypeModeller.addFixup(clazz, jType -> doubleCheckEmptyJson((JObject) jType, clazz),
				JacksonTypeModeller.MODEL_REVIEW_PRIORITY);
	}

	/**
	 * New up an instance of the class, and convert it to JSON
	 *
	 * @param clazz the class to consider
	 * @return a JSON string, or {@code null} if the JSON could not be produced.
	 */
	private static String createEmptyJsonFor(Class<?> clazz, SourcedType parent) {
		Object newInstance;
		try {
			Constructor<?> ctor = clazz.getConstructor();
			newInstance = ctor.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			logger.warn("Error instantiating class {} (path {})", clazz.getName(), parent.fullDescription(), e);
			return null;
		} catch (NoSuchMethodException e) {
			logger.warn("Class {} has no no-arg constructor (path {})", clazz.getName(), parent.fullDescription());
			return null;
		}
		try {
			// sort for output stability
			JsonMapper mapper = JsonMapper.builder().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true).build();
			return mapper.writeValueAsString(newInstance);
		} catch (JsonProcessingException e) {
			logger.warn("Error creating JSON for new {} (path {})", clazz.getName(), parent.fullDescription(), e);
			return null;
		}
	}

	/**
	 * New up an instance of the class, and convert it to JSON
	 *
	 * @param clazz the class to consider
	 * @return a JSON string, or {@code null} if the JSON could not be produced.
	 */
	private static String getTypeDiscriminatorValueFor(Class<?> clazz, BeanProperty property) {
		Object newInstance;
		try {
			newInstance = clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.warn("Error instantiating class {}", clazz.getName(), e);
			return null;
		} catch (NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		try {
			return (String) property.getMember().getValue(newInstance);
		} catch (Exception e) {
			logger.warn("Error retrieving value of type discriminator", e);
			return null;
		}
	}

	/**
	 * Sometimes the generated JSON for an empty object is illegal in the generated model (nullability at least, maybe more in the future).
	 * After the final model is constructed, re-validate the object, and remove it if it isn't valid anymore.
	 *
	 * @param jType the type
	 */
	private static void doubleCheckEmptyJson(JObject jType, Class<?> clazz) {
		String newObjectJson = jType.getNewObjectJson();
		if (newObjectJson != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String, Object> map = mapper.readValue(newObjectJson, new TypeReference<HashMap<String, Object>>() {
				});
				for (Map.Entry<String, JObject.Field> fieldEntry : jType.getFieldEntries()) {
					JObject.Field field = fieldEntry.getValue();
					if (field == null) {
						logger.warn("INTERNAL ERROR: field type is undefined, playing it safe");
						jType.setNewObjectJson(null);
						return;
					}
					JType fieldType = field.getType();
					boolean canBeNull = fieldType.canBeNull();
					boolean canBeUndefined = fieldType.canBeUndefined();
					String name = field.getName();
					Object o = map.get(name);
					if (!canBeNull && !canBeUndefined && o == null) {
						jType.setNewObjectJson(null);
						return;
					}
				}
			} catch (Exception e) {
				logger.error("Couldn't read generated new-object JSON for class {}", jType.getName(), e);
			}
		}
	}

	/**
	 * Take the bean property given back by Jackson, and put it into the object's definition. Figure out its type, and whether it's
	 * required.
	 *
	 * @param beanProperty the property
	 */
	private void handleField(BeanProperty beanProperty) {
		logger.debug("Handling bean property {} {}", beanProperty.getType(), beanProperty.getName());
		String name = beanProperty.getName();
		Type type;
		AnnotatedElement annotatedThing = beanProperty.getMember().getAnnotated();
		if (annotatedThing instanceof Field) {
			type = ((Field) annotatedThing).getGenericType();
		} else if (annotatedThing instanceof Method) {
			type = ((Method) annotatedThing).getGenericReturnType();
		} else {
			// Don't know how we would get here - property should be a field or getter
			throw new RuntimeException("Can't handle " + annotatedThing);
		}

		// if it's a Java primitive, override 'required'
		boolean required;
		if (type instanceof Class && ((Class<?>) type).isPrimitive()) {
			required = true;
		} else {
			required = beanProperty.isRequired();
		}

		logger.debug("Handling property {}.{}", jObject.getName(), name);
		// declare property now
		jObject.declareProperty(name);
		if (annotatedThing.isAnnotationPresent(TypeDiscriminator.class)) {
			TypeDiscriminator discriminator = annotatedThing.getAnnotation(TypeDiscriminator.class);
			jObject.setTypeDiscriminatorField(name);

			String discriminatorValue;
			if (discriminator.discriminatedBy() == DiscriminatedBy.CLASS_NAME) {
				discriminatorValue = jObject.getSourceClass().getName();
			} else {
				discriminatorValue = getTypeDiscriminatorValueFor(jObject.getSourceClass(), beanProperty);
			}
			logger.debug("Setting discriminator value on {} to {}", jObject, discriminatorValue);
			jObject.setTypeDiscriminatorValue(discriminatorValue);
			// whatever class is declaring this property, assume it's a superclass that we want to model in the output as a
			// 'superinterface'
			jacksonTypeModeller.queueType(new SourcedType(((Member) annotatedThing).getDeclaringClass(),
					"@TypeDiscriminator of property " + beanProperty.getFullName(), parent));
		}
		// actually define property at fixup time
		jacksonTypeModeller.addFixup(type, jType -> jObject.makeProperty(name, jType, required));
		// queue this property's type for processing
		jacksonTypeModeller.queueType(new SourcedType(type, "type of property " + beanProperty.getFullName(), parent));
	}

	/**
	 * Jackson event - property encountered
	 *
	 * @param prop the property
	 */
	@Override
	public void property(BeanProperty prop) {
		logger.debug("Callback property(BeanProperty) {}", prop.getName());
		handleField(prop);

	}

	/**
	 * Jackson event - non-POJO property encountered (see {@link JsonObjectFormatVisitor#property(String, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable, com.fasterxml.jackson.databind.JavaType)}
	 * <p>
	 * TODO implement
	 */
	@Override
	public void property(String name, JsonFormatVisitable handler, JavaType propertyTypeHint) {
		logger.debug("Callback property(name, handler, propertyTypeHint) {}", name);
		throw new RuntimeException("not implemented");
	}

	/**
	 * Jackson event - property encountered
	 *
	 * @param prop the property
	 */
	@Override
	public void optionalProperty(BeanProperty prop) {
		logger.debug("Callback optionalProperty(BeanProperty) {}", prop.getName());
		handleField(prop);

	}

	/**
	 * Jackson event - non-POJO property encountered (see {@link JsonObjectFormatVisitor#property(String, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable, com.fasterxml.jackson.databind.JavaType)}
	 * <p>
	 * TODO implement
	 */
	@Override
	public void optionalProperty(String name, JsonFormatVisitable handler, JavaType propertyTypeHint) {
		logger.debug("Callback optionalProperty(name, handler, propertyTypeHint) {}", name);
		throw new RuntimeException("not implemented");
	}

	@Override
	public JObject getResult() {
		return jObject;
	}
}

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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.ClassOverrideHandler;
import com.bluecirclesoft.open.jigen.model.JAny;
import com.bluecirclesoft.open.jigen.model.JArray;
import com.bluecirclesoft.open.jigen.model.JBoolean;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JMap;
import com.bluecirclesoft.open.jigen.model.JNumber;
import com.bluecirclesoft.open.jigen.model.JSpecialization;
import com.bluecirclesoft.open.jigen.model.JString;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.bluecirclesoft.open.jigen.model.JVoid;
import com.bluecirclesoft.open.jigen.model.JWildcard;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.model.PropertyEnumerator;
import com.bluecirclesoft.open.jigen.model.SourcedType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * <p>The JacksonTypeModeller coordinates the reading of a set of Java types, producing TypeScript types from them, and collecting the
 * resulting TS types into a Model.</p>
 * <p>This class uses a breadth-first search to find referenced types. When a type is processed, all the types it references are placed on a
 * queue, and the process is repeated until the queue is empty.</p>
 * <p>To ease the eventual reading of the model, I wanted to make sure that if a type in the TS model references another type, that it's
 * a direct reference, to avoid having to go to a lookup table or something like that.  This is necessary, in particular, for circular
 * references - one type clearly can't have a reference to another type that hasn't been digested yet. So to solve this, as types are
 * read, the modeller stores 'fixups' to join the types properly later. This creates two steps:</p>
 * <ol>
 * <li>All types are read, but references to other types are stubbed, and fixups are registered for later</li>
 * <li>The modeller runs all the fixups, connecting concrete JTypes with other concrete JTypes.</li>
 * </ol>
 *
 * @see Model
 */
public class JacksonTypeModeller implements PropertyEnumerator {

	@EqualsAndHashCode
	private static class FixupQueueItem implements Comparable<FixupQueueItem> {

		@Getter
		private final Type type;

		private final int priority;

		private final Consumer<JType> fixup;

		public FixupQueueItem(Type type, int priority, Consumer<JType> fixup) {
			this.type = type;
			this.priority = priority;
			this.fixup = fixup;
		}

		@Override
		public int compareTo(FixupQueueItem o) {
			return Integer.compare(getPriority(), o.getPriority());
		}

		Consumer<JType> getFixup() {
			return fixup;
		}

		int getPriority() {
			return priority;
		}

	}

	/**
	 * First pass of fixups - linking fields to their fully-defined types
	 */
	public static final int FIELD_FIXUP_PRIORITY = 1;

	/**
	 * Second pass of fixups - reviewing the model after it is fully defined
	 */
	public static final int MODEL_REVIEW_PRIORITY = 2;

	private static final Logger logger = LoggerFactory.getLogger(JacksonTypeModeller.class);

	/**
	 * Types that are intrinsic to TypeScript
	 */
	private static final Map<Class<?>, Supplier<JType>> FUNDAMENTAL_TYPES = new HashMap<>();

	static {
		FUNDAMENTAL_TYPES.put(Void.class, JVoid::new);
		FUNDAMENTAL_TYPES.put(Void.TYPE, JVoid::new);

		FUNDAMENTAL_TYPES.put(Boolean.class, JBoolean::new);
		FUNDAMENTAL_TYPES.put(Boolean.TYPE, JBoolean::new);

		FUNDAMENTAL_TYPES.put(Byte.class, JNumber::new);
		FUNDAMENTAL_TYPES.put(Byte.TYPE, JNumber::new);
		FUNDAMENTAL_TYPES.put(Short.class, JNumber::new);
		FUNDAMENTAL_TYPES.put(Short.TYPE, JNumber::new);
		FUNDAMENTAL_TYPES.put(Integer.class, JNumber::new);
		FUNDAMENTAL_TYPES.put(Integer.TYPE, JNumber::new);
		FUNDAMENTAL_TYPES.put(Long.class, JNumber::new);
		FUNDAMENTAL_TYPES.put(Long.TYPE, JNumber::new);
		FUNDAMENTAL_TYPES.put(Float.class, JNumber::new);
		FUNDAMENTAL_TYPES.put(Float.TYPE, JNumber::new);
		FUNDAMENTAL_TYPES.put(Double.class, JNumber::new);
		FUNDAMENTAL_TYPES.put(Double.TYPE, JNumber::new);
		FUNDAMENTAL_TYPES.put(BigInteger.class, JNumber::new);
		FUNDAMENTAL_TYPES.put(BigDecimal.class, JNumber::new);

		FUNDAMENTAL_TYPES.put(String.class, JString::new);
		FUNDAMENTAL_TYPES.put(Character.class, JString::new);
		FUNDAMENTAL_TYPES.put(Character.TYPE, JString::new);
	}

	/**
	 * Queue of pending types to process
	 */
	private final ArrayDeque<SourcedType> typesToProcess = new ArrayDeque<>();

	/**
	 * List of fixups to run after ingesting.
	 */
	private final PriorityQueue<FixupQueueItem> typeFixups = new PriorityQueue<>();

	private static final ReflectionsCache reflectionsCache = new ReflectionsCache();

	private final Reflections subclassFinder;

	private final ClassOverrideHandler classOverrides;

	private final IncludeSubclasses includeSubclasses;

	private final String[] packagesToScan;

	private final JEnum.EnumType defaultEnumType;

	/**
	 * Create a modeller.
	 */
	public JacksonTypeModeller(ClassOverrideHandler classOverrides, JEnum.EnumType defaultEnumType, IncludeSubclasses includeSubclasses,
	                           String[] packagesToScan) {
		assert defaultEnumType != null;
		assert packagesToScan != null;
		assert packagesToScan.length > 0;

		this.classOverrides = classOverrides;
		this.defaultEnumType = defaultEnumType;
		this.includeSubclasses = includeSubclasses;
		this.packagesToScan = packagesToScan;

		if (includeSubclasses == IncludeSubclasses.INCLUDE) {
			subclassFinder = reflectionsCache.getReflections(packagesToScan);
		} else {
			subclassFinder = null;
		}
		// clear list of created types (for debugging)
		JType.createdTypes.clear();
	}

	public static boolean isFundamentalType(Class<?> returnType) {
		return FUNDAMENTAL_TYPES.containsKey(returnType);
	}

	/**
	 * Add a fixup for a given type
	 *
	 * @param type     the type
	 * @param fixup    the fixup to apply
	 * @param priority the priority with which to run the fixup
	 */
	void addFixup(Type type, Consumer<JType> fixup, int priority) {
		typeFixups.add(new FixupQueueItem(type, priority, fixup));
	}

	/**
	 * Add a fixup for a given type (field fixup priority)
	 *
	 * @param type  the type
	 * @param fixup the fixup to apply
	 */
	void addFixup(Type type, Consumer<JType> fixup) {
		addFixup(type, fixup, FIELD_FIXUP_PRIORITY);
	}

	/**
	 * Queue a type for future processing
	 *
	 * @param sourcedType
	 */
	void queueType(SourcedType sourcedType) {
		typesToProcess.add(sourcedType);
	}

	/**
	 * Read Java types into the model.
	 *
	 * @param model        the model
	 * @param sourcedTypes the types to add
	 * @return a list of TS types added this run
	 */
	@Override
	public List<JType> readTypes(Model model, SourcedType... sourcedTypes) {

		// enqueue start types
		Collections.addAll(typesToProcess, sourcedTypes);

		// drain the queue
		while (!typesToProcess.isEmpty()) {
			SourcedType sourcedType = typesToProcess.pollFirst();
			if (model.hasType(sourcedType.getType())) {
				logger.debug("Type {} already seen", sourcedType);
				continue;
			}
			logger.debug("Type {} being added", sourcedType);
			try {
				model.addType(sourcedType.getType(), handleType(sourcedType));
			} catch (Throwable t) {
				throw new RuntimeException("Exception while processing " + sourcedType.fullDescription(), t);
			}
		}
		// apply fixups
		applyFixups(model, typeFixups);

		logger.debug("Done");

		// double-check that all our types wound up in the model
		Collection<JType> interfaces = model.getInterfaces();
		for (JType createdType : JType.createdTypes) {
			if (!interfaces.contains(createdType)) {
				throw new RuntimeException("Type " + createdType + " was created, but it's not in interfaces");
			}
		}

		// find all the TS types created for the type parameters, and return them, in the same order (necessary for #readOneType)
		List<JType> result = new ArrayList<>(sourcedTypes.length);
		for (SourcedType sourcedType : sourcedTypes) {
			result.add(model.getType(sourcedType.getType()));
		}
		return result;
	}

	private static void applyFixups(Model model, PriorityQueue<? extends FixupQueueItem> typeCleanups) {
		while (!typeCleanups.isEmpty()) {
			FixupQueueItem fixup = typeCleanups.poll();
			if (fixup != null) {
				JType jType = model.getType(fixup.getType());
				fixup.getFixup().accept(jType);
			}
		}
	}

	/**
	 * Convenience method to process just one type. This may, of course trigger the processing of other types by reference, and they'll
	 * be added to the model, but the other TS types won't be returned.
	 *
	 * @param model       the model
	 * @param sourcedType the Java type
	 * @return the TS type
	 */
	@Override
	public JType readOneType(Model model, SourcedType sourcedType) {
		return readTypes(model, sourcedType).get(0);
	}

	private JType handleType(SourcedType sourcedType) {
		Type type = sourcedType.getType();
		logger.debug("Handling {}", type);
		if (type instanceof TypeVariable) {
			// Case 1: type variable
			// Given a type like MyType<T>, the TypeVariable represents T. For a more complex instance like
			// MyType<T extends InterfaceA & InterfaceB>, then InterfaceA and InterfaceB will be in the 'intersectionBounds'. This can be
			// represented in TypeScript, and the syntax is the same.
			logger.debug("is TypeVariable");
			TypeVariable<?> variable = (TypeVariable<?>) type;
			JTypeVariable jVariable = new JTypeVariable(variable.getName());
			// if the bound is only Object, we're going to ignore it
			if (variable.getBounds().length != 1 || variable.getBounds()[0] != Object.class) {
				for (int i = 0; i < variable.getBounds().length; i++) {
					final int finalI = i;
					Type bound = variable.getBounds()[i];
					jVariable.getIntersectionBounds().add(null);
					queueType(new SourcedType(bound, "Type variable " + (i + 1), sourcedType));
					addFixup(bound, jType -> jVariable.getIntersectionBounds().set(finalI, jType));
				}
			}
			return jVariable;
		} else if (type instanceof ParameterizedType) {
			// Case 2: parametrized type
			// This is for the case of a generic type specialization, where the variables have been specified (e.g. List<Integer>)
			logger.debug("is ParameterizedType");
			ParameterizedType pt = (ParameterizedType) type;
			Type base = pt.getRawType();
			if (isCollection(base)) {
				// Collection<T> -> Array<T>
				JArray result = new JArray();
				result.setIndexType(new JNumber());
				addFixup(pt.getActualTypeArguments()[0], result::setElementType);
				queueType(new SourcedType(pt.getActualTypeArguments()[0], "Collection value type", sourcedType));
				return result;
			} else if (isMap(base)) {
				// Map<String, T> -> { [key: string]: T }
				JMap result = new JMap();
				addFixup(pt.getActualTypeArguments()[1], result::setValueType);
				queueType(new SourcedType(pt.getActualTypeArguments()[1], "Map value type", sourcedType));
				return result;
			} else {
				// everything else: A<B> -> A<B>
				JSpecialization jSpecialization = new JSpecialization();
				jSpecialization.setParameters(new JType[pt.getActualTypeArguments().length]);
				addFixup(pt.getRawType(), jSpecialization::setBase);
				queueType(new SourcedType(pt.getRawType(), "Base type", sourcedType));
				for (int i = 0; i < pt.getActualTypeArguments().length; i++) {
					final int finalI = i;
					addFixup(pt.getActualTypeArguments()[i], jType -> jSpecialization.getParameters()[finalI] = jType);
					queueType(new SourcedType(pt.getActualTypeArguments()[i], "Type argument " + (i + 1), sourcedType));
				}
				return jSpecialization;
			}
		} else if (type instanceof WildcardType) {
			// Case 3: wildcard type
			// This is for the case of a generic type specialization, where the variables have been specified (e.g. List<Integer>)
			logger.debug("is WildcardType");
			WildcardType pt = (WildcardType) type;
			JWildcard jWildcard = new JWildcard();
			Type[] upperBounds = pt.getUpperBounds();
			for (int i = 0; i < upperBounds.length; i++) {
				Type upper = upperBounds[i];
				jWildcard.getUpperBounds().add(null);
				final int idx = i;
				addFixup(upper, (f) -> jWildcard.getUpperBounds().set(idx, f));
				queueType(new SourcedType(upper, "Upper bound " + (i + 1), sourcedType));
			}
			Type[] lowerBounds = pt.getLowerBounds();
			for (int i = 0; i < lowerBounds.length; i++) {
				Type lower = lowerBounds[i];
				jWildcard.getLowerBounds().add(null);
				final int idx = i;
				addFixup(lower, (f) -> jWildcard.getLowerBounds().set(idx, f));
				queueType(new SourcedType(lower, "Lower bound " + (i + 1), sourcedType));
			}
			return jWildcard;
		} else if (type instanceof Class) {
			// Case 3: class (non-generic, plain vanilla)
			logger.debug("is Class");
			Class<?> cl = (Class<?>) type;
			if (cl.isArray()) {
				// array: A[] -> Array<A>
				logger.debug("is array");
				JArray result = new JArray();
				result.setIndexType(new JNumber());
				addFixup(cl.getComponentType(), result::setElementType);
				queueType(new SourcedType(cl.getComponentType(), "Array element type", sourcedType));
				return result;
			} else {
				if (classOverrides.containsKey(cl)) {
					// substitute class.  If the user created a cycle, sorry.
					Class<?> classOverride = classOverrides.get(cl);
					return handleType(new SourcedType(classOverride, "Class override of " + cl, sourcedType));
				} else if (FUNDAMENTAL_TYPES.containsKey(type)) {
					// built-in type: int -> number, etc
					logger.debug("is fundamental type");
					return FUNDAMENTAL_TYPES.get(type).get();
				} else {
					// everything else -> interface
					logger.debug("is user-defined class");
					if (includeSubclasses == IncludeSubclasses.INCLUDE) {
						enqueueSubclasses((Class<?>) type, sourcedType);
					}
					return handleUserDefinedClass((Class<?>) type, sourcedType);
				}
			}
		} else {
			throw new RuntimeException("Can't handle " + type);
		}
	}

	private void enqueueSubclasses(Class<?> type, SourcedType parent) {
		if (subclassFinder != null) {
			if (type != Object.class) {
				Set<Class<?>> subtypes = subclassFinder.getSubTypesOf((Class<Object>) type);
				for (Class<?> cl : subtypes) {
					queueType(new SourcedType(cl, "Subclass of " + type, parent));
				}
			}
		}
	}

	/**
	 * Is this Java type a Map? (will turn into an object)
	 *
	 * @param base the type
	 * @return yes or no
	 */
	private static boolean isMap(Type base) {
		return base instanceof Class && Map.class.isAssignableFrom((Class<?>) base);
	}

	/**
	 * Is this Java type a collection? (will turn into an array)
	 *
	 * @param base the type
	 * @return yes or no
	 */
	private static boolean isCollection(Type base) {
		return base instanceof Class && Collection.class.isAssignableFrom((Class<?>) base);
	}

	private JType handleUserDefinedClass(Class<?> type, SourcedType parent) {

		// Run a Jackson JsonFormatVisitor over our class, let it process all the object properties, and return the resulting JType.
		TypeReadingVisitor<?> reader;
		try {
			ClassReader wrapper = new ClassReader(parent);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.acceptJsonFormatVisitor(type, wrapper);
			reader = wrapper.getReader();
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		}
		if (reader == null) {
			return null;
		} else {
			return reader.getResult();
		}
	}

	/**
	 * Jackson format visitor which will be invoked over all the properties.
	 */
	private class ClassReader extends JsonFormatVisitorWrapper.Base {

		private TypeReadingVisitor<?> reader;

		private final SourcedType parent;

		public ClassReader(SourcedType parent) {
			this.parent = parent;
		}

		TypeReadingVisitor<?> getReader() {
			return reader;
		}

		@Override
		public JsonArrayFormatVisitor expectArrayFormat(JavaType type) {
			return new JsonArrayFormatVisitor.Base() {
				@Override
				public void itemsFormat(JsonFormatVisitable handler, JavaType elementType) throws JsonMappingException {
					super.itemsFormat(handler, elementType);
				}

				@Override
				public void itemsFormat(JsonFormatTypes format) throws JsonMappingException {
					super.itemsFormat(format);
				}
			};
		}

		@Override
		public JsonStringFormatVisitor expectStringFormat(JavaType type) {
			reader = JString::new;
			return new JsonStringFormatVisitor.Base() {
				@Override
				public void enumTypes(Set<String> enums) {
					reader = () -> {
						Class<?> rawClass = type.getRawClass();
						return buildEnum(rawClass);
					};
				}
			};
		}

		@Override
		public JsonNumberFormatVisitor expectNumberFormat(JavaType type) {
			reader = JNumber::new;
			return new JsonNumberFormatVisitor() {
				@Override
				public void numberType(JsonParser.NumberType type) {
					throw new RuntimeException("not implemented");
				}

				@Override
				public void format(JsonValueFormat format) {
					throw new RuntimeException("not implemented");
				}

				@Override
				public void enumTypes(Set<String> enums) {
					throw new RuntimeException("not implemented");
				}
			};
		}

		@Override
		public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) {
			reader = JNumber::new;
			return new JsonIntegerFormatVisitor.Base() {
			};
		}

		@Override
		public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) {
			reader = JNumber::new;
			return new JsonBooleanFormatVisitor.Base() {
			};
		}

		@Override
		public JsonNullFormatVisitor expectNullFormat(JavaType type) {
			reader = JNumber::new;
			return new JsonNullFormatVisitor.Base() {
			};
		}

		@Override
		public JsonAnyFormatVisitor expectAnyFormat(JavaType type) {
			reader = JAny::new;
			return new JsonAnyFormatVisitor.Base() {
			};
		}

		@Override
		public JsonMapFormatVisitor expectMapFormat(JavaType type) {
			// We would only have gotten here for a raw map; it's element type is any
			JMap map = new JMap(new JAny());
			reader = () -> map;
			return new JsonMapFormatVisitor.Base() {
			};
		}

		@Override
		public JsonObjectFormatVisitor expectObjectFormat(JavaType type) {
			JsonObjectReader myReader = new JsonObjectReader(JacksonTypeModeller.this, type.getRawClass(), parent);
			reader = myReader;
			return myReader;
		}
	}

	private JEnum buildEnum(Class<?> rawClass) {
		JEnum.EnumType enumType = null;
		List<JEnum.EnumDeclaration> entries = new ArrayList<>();
		Enum<?>[] constants = (Enum<?>[]) rawClass.getEnumConstants();
		Map<String, Enum<?>> usedValues = new HashMap<>();
		for (Enum<?> enumConstant : constants) {

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String enumConstantValue = objectMapper.writeValueAsString(enumConstant);
				// strip quotes
				int length = enumConstantValue.length();
				if (length > 1 && enumConstantValue.charAt(0) == '"') {
					enumConstantValue = enumConstantValue.substring(1, length - 1);
					if (enumType != null && enumType != JEnum.EnumType.STRING) {
						throw new RuntimeException("Jackson giving both numeric and string values for enum " + rawClass.getName());
					}
					enumType = JEnum.EnumType.STRING;
				} else {
					if (enumType != null && enumType != JEnum.EnumType.NUMERIC) {
						throw new RuntimeException("Jackson giving both numeric and string values for enum " + rawClass.getName());
					}
					enumType = JEnum.EnumType.NUMERIC;
				}

				if (usedValues.containsKey(enumConstantValue)) {
					logger.warn("Computed serialized value of {} to be {}, but that value was also used for {} " +
							"- keeping first definition only", enumConstant, enumConstantValue, usedValues.get(enumConstantValue));
				} else {
					usedValues.put(enumConstantValue, enumConstant);
				}
				entries.add(new JEnum.EnumDeclaration(enumConstant.name(), enumConstant.ordinal(), enumConstantValue));
			} catch (JsonProcessingException e) {
				logger.warn("Could not serialize {}, skipping", enumConstant);
			}
		}
		return new JEnum(rawClass.getName(), enumType == null ? defaultEnumType : enumType, entries);
	}
}

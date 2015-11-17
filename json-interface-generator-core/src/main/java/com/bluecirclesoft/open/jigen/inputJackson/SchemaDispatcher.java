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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.AnySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.BooleanSchema;
import com.fasterxml.jackson.module.jsonSchema.types.HyperSchema;
import com.fasterxml.jackson.module.jsonSchema.types.IntegerSchema;
import com.fasterxml.jackson.module.jsonSchema.types.NullSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ReferenceSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.fasterxml.jackson.module.jsonSchema.types.UnionTypeSchema;

/**
 * TODO document me
 */
abstract public class SchemaDispatcher<T> {

	private static final Logger logger = LoggerFactory.getLogger(SchemaDispatcher.class);

	public final T dispatch(JsonSchema schema) {
		logger.info("Processing schema {}", toString(schema));
		if (schema.isAnySchema()) {
			return handle(schema.asAnySchema());
		} else if (schema.isArraySchema()) {
			return handle(schema.asArraySchema());
		} else if (schema.isBooleanSchema()) {
			return handle(schema.asBooleanSchema());
		} else if (schema.isIntegerSchema()) {
			return handle(schema.asIntegerSchema());
		} else if (schema.isNullSchema()) {
			return handle(schema.asNullSchema());
		} else if (schema.isObjectSchema()) {
			return handle(schema.asObjectSchema());
		} else if (schema.isStringSchema()) {
			return handle(schema.asStringSchema());
		} else if (schema.isUnionTypeSchema()) {
			return handle(schema.asUnionTypeSchema());
		} else if (schema instanceof HyperSchema) {
			return handle((HyperSchema) schema);
		} else if (schema instanceof ReferenceSchema) {
			return handle((ReferenceSchema) schema);
		} else {
			throw new RuntimeException("Can't dispatch schema of type " + schema.getClass().getName());
		}
	}

	private String toString(JsonSchema schema) {
		return "{schema id: " + schema.getId() + ", type: " + schema.getType().value() + ", description: " + schema.getDescription() + "}";
	}

	protected abstract T handle(ReferenceSchema schema);

	protected abstract T handle(HyperSchema schema);

	protected abstract T handle(UnionTypeSchema unionTypeSchema);

	protected abstract T handle(StringSchema stringSchema);

	protected abstract T handle(ObjectSchema objectSchema);

	protected abstract T handle(NullSchema nullSchema);

	protected abstract T handle(IntegerSchema integerSchema);

	protected abstract T handle(BooleanSchema booleanSchema);

	protected abstract T handle(ArraySchema arraySchema);

	protected abstract T handle(AnySchema anySchema);

}

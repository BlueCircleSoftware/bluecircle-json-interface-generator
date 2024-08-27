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
 *
 */

package com.bluecirclesoft.open.jigen.typescript;

import com.bluecirclesoft.open.jigen.model.JAny;
import com.bluecirclesoft.open.jigen.model.JArray;
import com.bluecirclesoft.open.jigen.model.JBoolean;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JMap;
import com.bluecirclesoft.open.jigen.model.JNull;
import com.bluecirclesoft.open.jigen.model.JNumber;
import com.bluecirclesoft.open.jigen.model.JObject;
import com.bluecirclesoft.open.jigen.model.JSpecialization;
import com.bluecirclesoft.open.jigen.model.JString;
import com.bluecirclesoft.open.jigen.model.JType;
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.bluecirclesoft.open.jigen.model.JTypeVisitor;
import com.bluecirclesoft.open.jigen.model.JUnionType;
import com.bluecirclesoft.open.jigen.model.JVoid;
import com.bluecirclesoft.open.jigen.model.JWildcard;
import com.bluecirclesoft.open.jigen.model.Namespace;

/**
 * TODO document me
 */
class TypeUsageProducer {

	public enum WillBeSpecialized {
		YES,
		NO
	}

	public enum UseImmutableSuffix {
		YES,
		NO
	}

	private final String immutableSuffix;

	private final WillBeSpecialized isSpecializing;

	private final UseImmutableSuffix useImmutableSuffix;

	private final boolean treatNullAsUndefined;

	private final UnknownProducer unknownProducer;

	private final Options options;

	public TypeUsageProducer(Options options, UseImmutableSuffix useImmutableSuffix) {
		this(options, WillBeSpecialized.NO, useImmutableSuffix);
	}

	public TypeUsageProducer(Options options, WillBeSpecialized isSpecializing, UseImmutableSuffix useImmutableSuffix) {
		assert options != null : "options is null";
		this.options = options;
		this.useImmutableSuffix = useImmutableSuffix;
		this.immutableSuffix = useImmutableSuffix == UseImmutableSuffix.YES ? options.getImmutableSuffix() : null;
		this.isSpecializing = isSpecializing;
		this.treatNullAsUndefined = options.isNullIsUndefined();
		this.unknownProducer = new UnknownProducer(options);
	}

	public JTypeVisitor<String> getProducer(Namespace referenceLocation, TSFileWriter writer) {

		return new JTypeVisitor<String>() {

			@Override
			public String visit(JObject jObject) {
				String prefix = writer.getReferencePrefix(referenceLocation, jObject.getContainingNamespace());
				String refStr = prefix + jObject.getName() + (immutableSuffix != null ? immutableSuffix : "");
				if (isSpecializing == WillBeSpecialized.YES) {
					// produced as part of a JSpecialization, which will output its own type parameters
					return refStr;
				} else {
					if (jObject.getTypeVariables().isEmpty()) {
						// not a specialization, but no type parameters
						return refStr;
					} else {
						// not specializing, no type parameters.  TypeScript will require type parameters, so we need to put
						// the Java equivalent for the type parameters, which is 'any'
						StringBuilder sb = buildParameterizedType(jObject, refStr);
						return sb.toString();
					}
				}
			}

			@Override
			public String visit(JAny jAny) {
				return unknownProducer.getUnknown();
			}

			@Override
			public String visit(JArray jArray) {
				return jArray.getElementType().accept(this) + "[]";
			}

			@Override
			public String visit(JBoolean jBoolean) {
				return "boolean";
			}

			@Override
			public String visit(JEnum jEnum) {
				String prefix = writer.getReferencePrefix(referenceLocation, jEnum.getContainingNamespace());
				return prefix + jEnum.getName();
			}

			@Override
			public String visit(JNumber jNumber) {
				return "number";
			}

			@Override
			public String visit(JString jString) {
				return "string";
			}

			@Override
			public String visit(JVoid jVoid) {
				return "void";
			}

			@Override
			public String visit(JSpecialization jSpecialization) {
				if (!jSpecialization.getBase().isSpecializable()) {
					return jSpecialization.getBase().accept(this);
				}

				StringBuilder sb = new StringBuilder();
				TypeUsageProducer subTup = new TypeUsageProducer(options, WillBeSpecialized.YES, useImmutableSuffix);
				sb.append(jSpecialization.getBase().accept(subTup.getProducer(referenceLocation, writer)));
				sb.append("<");
				boolean needsComma = false;
				for (JType param : jSpecialization.getParameters()) {
					if (needsComma) {
						sb.append(", ");
					} else {
						needsComma = true;
					}
					sb.append(param.accept(this));
				}
				sb.append(">");
				return sb.toString();
			}

			@Override
			public String visit(JTypeVariable jTypeVariable) {
				return jTypeVariable.getName();
			}

			@Override
			public String visit(JMap jMap) {
				return "{[name: string]:" + jMap.getValueType().accept(this) + "}";
			}

			@Override
			public String visit(JUnionType jUnionType) {
				// get the strings for all the member types, and join them together with vertical bars
				StringBuilder sb = new StringBuilder();
				for (JType member : jUnionType.getMembers()) {
					if (sb.length() > 0) {
						sb.append(" | ");
					}
					sb.append(member.accept(this));
				}
				return sb.toString();
			}

			@Override
			public String visit(JNull jNull) {
				if (treatNullAsUndefined) {
					return "null | undefined";
				} else {
					return "null";
				}
			}

			@Override
			public String visit(JWildcard jWildcard) {
				// TypeScript doesn't have the concept of wildcard types, or of lower bounds. So I'll just convert the upper bounds and leave
				// the rest, on the theory that it's better than nothing
				if (jWildcard.getUpperBounds().isEmpty()) {
					return unknownProducer.getUnknown();
				} else {
					StringBuilder sb = new StringBuilder();
					for (JType bound : jWildcard.getUpperBounds()) {
						if (sb.length() > 0) {
							sb.append(" & ");
						}
						sb.append(bound.accept(this));
					}
					return sb.toString();
				}
			}
		};
	}

	private StringBuilder buildParameterizedType(JObject jObject, String refStr) {
		StringBuilder sb = new StringBuilder();
		sb.append(refStr);
		sb.append("<");
		boolean needsComma = false;
		int count = jObject.getTypeVariables().size();
		for (int i = 0; i < count; i++) {
			if (needsComma) {
				sb.append(", ");
			} else {
				needsComma = true;
			}
			sb.append(unknownProducer.getUnknown());
		}
		sb.append(">");
		return sb;
	}
}

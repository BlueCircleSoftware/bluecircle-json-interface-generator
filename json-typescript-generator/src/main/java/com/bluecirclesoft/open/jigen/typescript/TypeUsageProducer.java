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

/**
 * TODO document me
 */
class TypeUsageProducer implements JTypeVisitor<String> {

	private final String immutableSuffix;

	private final WillBeSpecialized isSpecializing;

	private final boolean treatNullAsUndefined;

	private final boolean useUnknown;

	public enum WillBeSpecialized {
		YES,
		NO
	}

	public TypeUsageProducer(String immutableSuffix, boolean treatNullAsUndefined, boolean useUnknown) {
		this(immutableSuffix, WillBeSpecialized.NO, treatNullAsUndefined, useUnknown);
	}

	public TypeUsageProducer(String immutableSuffix, WillBeSpecialized isSpecializing, boolean treatNullAsUndefined, boolean useUnknown) {
		this.immutableSuffix = immutableSuffix;
		this.isSpecializing = isSpecializing;
		this.treatNullAsUndefined = treatNullAsUndefined;
		this.useUnknown = useUnknown;
	}

	@Override
	public String visit(JObject jObject) {
		String refStr = jObject.getReference() + (immutableSuffix != null ? immutableSuffix : "");
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
					sb.append(useUnknown ? "unknown" : "any");
				}
				sb.append(">");
				return sb.toString();
			}
		}
	}

	@Override
	public String visit(JAny jAny) {
		return useUnknown ? "unknown" : "any";
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
		return jEnum.getReference();
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
		StringBuilder sb = new StringBuilder();
		sb.append(jSpecialization.getBase()
				.accept(new TypeUsageProducer(immutableSuffix, WillBeSpecialized.YES, treatNullAsUndefined, useUnknown)));
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
			return useUnknown ? "unknown" : "any";
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
}

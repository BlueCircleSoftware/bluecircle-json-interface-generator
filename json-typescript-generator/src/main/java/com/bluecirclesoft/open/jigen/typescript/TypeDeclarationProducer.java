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

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

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
class TypeDeclarationProducer implements JTypeVisitor<Integer> {

	private final TSFileWriter writer;

	private final boolean produceImmutable;

	private final boolean treatNullAsUndefined;

	private final UnknownProducer unknownProducer;

	private final String immutableSuffix;

	private final Options options;

	public TypeDeclarationProducer(TSFileWriter writer, Options options) {
		this.writer = writer;
		this.options = options;
		this.produceImmutable = options.isProduceImmutables();
		this.treatNullAsUndefined = options.isNullIsUndefined();
		this.unknownProducer = new UnknownProducer(options);
		this.immutableSuffix = options.getImmutableSuffix();
	}

	@Override
	public Integer visit(JObject jObject) {
		makeInterfaceDeclaration(jObject);
		return null;
	}

	@Override
	public Integer visit(JAny jAny) {
		return null;
	}

	@Override
	public Integer visit(JArray jArray) {
		return null;
	}

	@Override
	public Integer visit(JBoolean jBoolean) {
		return null;
	}

	@Override
	public Integer visit(JEnum jEnum) {
		writer.line();
		String name = jEnum.getName();
		// emit enum
		writer.line("export enum " + name + " {");
		writer.indentIn();
		int count = 0;
		List<JEnum.EnumDeclaration> values = jEnum.getValues();
		int valueCount = values.size() - 1;
		for (JEnum.EnumDeclaration value : values) {
			String suffix;
			if (count == valueCount) {
				suffix = "";
			} else {
				suffix = ",";
			}
			if (jEnum.getEnumType() == JEnum.EnumType.STRING) {
				writer.line(value.getName() + " = \"" + StringEscapeUtils.escapeEcmaScript(value.getSerializedValue()) + "\"" + suffix);
			} else {
				writer.line(value.getName() + suffix);
			}
			count++;
		}
		writer.indentOut();
		writer.line("}");
		// enum already has index -> name and name -> index, but we will emit index -> enum constant and name -> enum constant
		writer.addImport("jsonInterfaceGenerator", jEnum.getContainingNamespace(), writer.getJIGNamespace());
		writer.line("export const " + name + "_values : jsonInterfaceGenerator.EnumReverseLookup<" + name + "> = {};");
		for (JEnum.EnumDeclaration value : values) {
			writer.line(name + "_values[" + value.getNumericValue() + "] = " + name + "." + value.getName() + ";");
			writer.line(name + "_values[\"" + StringEscapeUtils.escapeEcmaScript(value.getSerializedValue()) + "\"] = " + name + "." +
					value.getName() + ";");
		}
		return null;
	}

	@Override
	public Integer visit(JNumber jNumber) {
		return null;
	}

	@Override
	public Integer visit(JString jString) {
		return null;
	}

	@Override
	public Integer visit(JVoid jVoid) {
		return null;
	}

	@Override
	public Integer visit(JSpecialization jSpecialization) {
		return null;
	}

	@Override
	public Integer visit(JTypeVariable jTypeVariable) {
		return null;
	}

	@Override
	public Integer visit(JMap jMap) {
		return null;
	}

	@Override
	public Integer visit(JUnionType jUnionType) {
		return null;
	}

	@Override
	public Integer visit(JNull jNull) {
		return null;
	}

	@Override
	public Integer visit(JWildcard jWildcard) {
		return null;
	}


	private void makeInterfaceDeclaration(JObject intf) {
		String interfaceLabel = intf.getName();

		Set<String> subTypeValues = null;

		String definterfaceType = intf.accept(
				new TypeVariableProducer(UsageLocation.DEFINITION, null, unknownProducer, intf.getContainingNamespace(), writer));
		TypeUsageProducer typeUsageProducer = new TypeUsageProducer(options, TypeUsageProducer.UseImmutableSuffix.NO);
		writer.line();
		StringBuilder declLine = new StringBuilder("export interface " + definterfaceType);
		if (intf.getSuperclasses().size() > 0) {
			declLine.append(" extends ");
			boolean needsComma = false;
			for (Map.Entry<String, JObject> entry : intf.getSuperclasses().entrySet()) {
				if (needsComma) {
					declLine.append(", ");
				} else {
					needsComma = true;
				}
				declLine.append(typeUsageProducer.getProducer(intf.getContainingNamespace(), writer).visit(entry.getValue()));
			}
		}
		declLine.append(" {");
		writer.line(declLine.toString());
		writer.indentIn();

		// if we pass true here for treatNullAsUndefined, then we might get what seems like an extra
		// 'undefined' specification for nullable fields, like this:
		//    doubleA?: string | null | undefined;
		// where doubleA is specified as undefined by both the question mark and the type union. But
		// if we don't, it can't propagate down to type parameters, etc.  So I'm okay with it.
		for (Map.Entry<String, JObject.Field> prop : intf.getFieldEntries()) {
			String makeOptional = "";
			String typeString;
			if (Objects.equals(intf.getTypeDiscriminatorField(), prop.getKey())) {
				// is type discriminator - type will be the values
				subTypeValues = collectTypeValues(intf);
				StringBuilder typeBuilder = new StringBuilder();
				boolean needsOr = false;
				for (String value : subTypeValues) {
					if (needsOr) {
						typeBuilder.append(" | ");
					} else {
						needsOr = true;
					}
					typeBuilder.append('"');
					typeBuilder.append(value);
					typeBuilder.append('"');
				}
				typeString = typeBuilder.toString();
			} else {
				// otherwise normal field
				JType type = prop.getValue().getType();
				if (type.canBeUndefined() || (treatNullAsUndefined && type.canBeNull())) {
					makeOptional = "?";
				}
				typeString = type.accept(typeUsageProducer.getProducer(intf.getContainingNamespace(), writer));
			}
			writer.line(prop.getKey() + makeOptional + ": " + typeString + ";");
		}
		writer.indentOut();
		writer.line("}");

		String typeVars = "";
		String unknownTypeVars = "";
		if (!intf.getTypeVariables().isEmpty()) {
			StringBuilder typeVarsBuilder = new StringBuilder();
			StringBuilder unknownTypeVarsBuilder = new StringBuilder();
			typeVarsBuilder.append('<');
			unknownTypeVarsBuilder.append('<');
			boolean needsComma = false;
			for (JTypeVariable var : intf.getTypeVariables()) {
				if (needsComma) {
					typeVarsBuilder.append(',');
					unknownTypeVarsBuilder.append(',');
				} else {
					needsComma = true;
				}
				typeVarsBuilder.append(var.getName());
				// TODO I'm not sure I'm satisfied with this - will this short-circuit type analysis
				// for the isInstance methods?
				unknownTypeVarsBuilder.append("any");
			}
			typeVarsBuilder.append('>');
			unknownTypeVarsBuilder.append('>');
			typeVars = typeVarsBuilder.toString();
			unknownTypeVars = unknownTypeVarsBuilder.toString();
		}

		boolean hasNewObjectJson = StringUtils.isNotBlank(intf.getNewObjectJson());
		boolean hasTypeDiscriminator = intf.getTypeDiscriminatorField() != null;

		boolean needsNamespace = hasNewObjectJson || hasTypeDiscriminator;
		if (needsNamespace) {
			String nsLine = "export namespace " + interfaceLabel + " {";
			writer.line(nsLine);
			writer.indentIn();
		}

		// Copy type variables from definition
		String makeTypeVars;
		int anglePos = definterfaceType.indexOf('<');
		if (anglePos >= 0) {
			makeTypeVars = definterfaceType.substring(anglePos);
		} else {
			makeTypeVars = "";
		}

		if (hasTypeDiscriminator) {
			writer.line("export function getDiscriminator() : \"" + intf.getTypeDiscriminatorValue() + "\" {");
			writer.indentIn();
			writer.line("return \"" + intf.getTypeDiscriminatorValue() + "\";");
			writer.indentOut();
			writer.line("}");
		}

		if (hasNewObjectJson) {
			if (hasTypeDiscriminator) {
				writer.line("export function make" + makeTypeVars + "(initial?: Omit<" + interfaceLabel + typeVars + ", \"" +
						intf.getTypeDiscriminatorField() + "\">) : " + interfaceLabel + typeVars + " {");
				writer.indentIn();
				writer.line("if (initial) {");
				writer.indentIn();
				writer.line("return {" + intf.getTypeDiscriminatorField() + ": getDiscriminator(), ...initial};");
				writer.indentOut();
				writer.line("} else {");
				writer.indentIn();
				writer.line("return " + intf.getNewObjectJson() + ";");
				writer.indentOut();
				writer.line("}");
				writer.indentOut();
				writer.line("}");
			} else {
				writer.line("export function make" + makeTypeVars + "() : " + interfaceLabel + typeVars + " {");
				writer.indentIn();
				writer.line("return " + intf.getNewObjectJson() + ";");
				writer.indentOut();
				writer.line("}");
			}
		} else {
			if (hasTypeDiscriminator) {
				writer.line("export function make" + makeTypeVars + "(initial: Omit<" + interfaceLabel + typeVars + ", \"" +
						intf.getTypeDiscriminatorField() + "\">) : " + interfaceLabel + typeVars + " {");
				writer.indentIn();
				writer.line("return {" + intf.getTypeDiscriminatorField() + ": getDiscriminator(), ...initial};");
				writer.indentOut();
				writer.line("}");

			}
		}

		if (hasTypeDiscriminator && subTypeValues != null) {
			writer.line("const TYPE_REGEX = new RegExp(\"" + createTypeRegex(subTypeValues) + "\");");
			writer.line(
					"export function isInstance(obj: " + unknownProducer.getUnknown() + "): obj is " + interfaceLabel + unknownTypeVars +
							" {");
			writer.indentIn();
			writer.line("return typeof obj === \"object\" && !Array.isArray(obj) && !!(obj as {[k:string]:any})[\"" +
					intf.getTypeDiscriminatorField() + "\"] && TYPE_REGEX.exec((obj as {[k:string]:any})[\"" +
					intf.getTypeDiscriminatorField() + "\"]) !== null;");
			writer.indentOut();
			writer.line("}");
		}

		if (needsNamespace) {
			writer.indentOut();
			writer.line("}");
		}

		if (produceImmutable) {
			writer.addImport("jsonInterfaceGenerator", intf.getContainingNamespace(), writer.getJIGNamespace());
			String immutableInterfaceType = intf.accept(
					new TypeVariableProducer(UsageLocation.DEFINITION, "$Imm", unknownProducer, intf.getContainingNamespace(), writer));
			declLine = new StringBuilder("export class " + immutableInterfaceType + " {");
			writer.line(declLine.toString());
			writer.indentIn();

			String localChangeWrapper = "jsonInterfaceGenerator.ChangeWrapper<" + interfaceLabel + typeVars + ">";
			String localChangeWatcher = "jsonInterfaceGenerator.ChangeWatcher<" + interfaceLabel + typeVars + ">";

			writer.line("private _delegate: " + localChangeWrapper + ";");
			writer.line("public constructor(base: jsonInterfaceGenerator.ChangeRoot<jsonInterfaceGenerator.UnknownType>, path?: " +
					"jsonInterfaceGenerator" + ".SelectorList) {");
			writer.indentIn();
			writer.line("this._delegate = new " + localChangeWrapper + "(base, path);");
			writer.indentOut();
			writer.line("}");
			writer.line("public $watch" + "(watcher: " + localChangeWatcher + "): void;");
			writer.line("public $watch" + "<K extends keyof " + interfaceLabel + typeVars + ">(key: K, watcher: jsonInterfaceGenerator." +
					"ChangeWatcher<" + interfaceLabel + typeVars + "[K]>): void;");
			writer.line(
					"public $watch" + "(keyOrWatcher: string | " + localChangeWatcher + ", watcher?: " + localChangeWatcher + "): void {");
			writer.indentIn();
			writer.line("if (typeof keyOrWatcher===\"string\") {");
			writer.indentIn();
			writer.line(
					"this._delegate.watchSub(keyOrWatcher as string, watcher as jsonInterfaceGenerator.ChangeWatcher<" + interfaceLabel +
							typeVars + ">);");
			writer.indentOut();
			writer.line("} else {");
			writer.indentIn();
			writer.line("this._delegate.watch(keyOrWatcher as " + localChangeWatcher + ");");
			writer.indentOut();
			writer.line("}");
			writer.indentOut();
			writer.line("}");
			writer.line("public $unwatch" + "(watcher: " + localChangeWatcher + "): " + "void;");
			writer.line("public $unwatch" + "<K extends keyof " + interfaceLabel + typeVars + ">(key: K, watcher: jsonInterfaceGenerator." +
					"ChangeWatcher<" + interfaceLabel + typeVars + "[K]>): void;");
			writer.line("public $unwatch" + "(keyOrWatcher: string | " + localChangeWatcher + ", watcher?: " + localChangeWatcher +
					"): void {");
			writer.indentIn();
			writer.line("if (typeof keyOrWatcher===\"string\") {");
			writer.indentIn();
			writer.line("this._delegate.unwatchSub(keyOrWatcher as string, watcher as " + localChangeWatcher + ");");
			writer.indentOut();
			writer.line("} else {");
			writer.indentIn();
			writer.line("this._delegate.unwatch(keyOrWatcher as " + localChangeWatcher + ");");
			writer.indentOut();
			writer.line("}");
			writer.indentOut();
			writer.line("}");
			writer.line("public get " + "$self" + "() : Readonly<" + interfaceLabel + typeVars + "> {");
			writer.indentIn();
			writer.line("return this._delegate.get() as Readonly<" + interfaceLabel + typeVars + ">;");
			writer.indentOut();
			writer.line("}");
			writer.line("public set " + "$self" + "(v : Readonly<" + interfaceLabel + typeVars + ">) {");
			writer.indentIn();
			writer.line("this._delegate.set(v);");
			writer.indentOut();
			writer.line("}");
			for (Map.Entry<String, JObject.Field> prop : intf.getFieldEntries()) {
				AccessorProducer accessorProducer = new AccessorProducer(prop.getKey(), writer, options, intf.getContainingNamespace());
				prop.getValue().getType().accept(accessorProducer);
			}
			writer.indentOut();
			writer.line("}");
		}
	}

	private String createTypeRegex(Set<String> subTypeStrings) {
		StringBuilder result = new StringBuilder();
		result.append("^(");
		boolean needsBar = false;
		for (String string : subTypeStrings) {
			if (needsBar) {
				result.append("|");
			} else {
				needsBar = true;
			}
			result.append(string.replace(".", "\\."));
		}
		result.append(")$");
		return result.toString();
	}

	private Set<String> collectTypeValues(JObject intf) {
		ArrayDeque<JObject> queue = new ArrayDeque<>();
		Set<String> result = new LinkedHashSet<>();
		queue.add(intf);
		while (!queue.isEmpty()) {
			JObject obj = queue.pollFirst();
			if (obj == null) {
				throw new RuntimeException("Internal error: null subclass in subtree of" + intf);
			}
			String type = obj.getTypeDiscriminatorValue();
			if (StringUtils.isBlank(type)) {
				throw new RuntimeException("Type discriminator value is null in " + obj);
			}
			if (result.contains(type)) {
				throw new RuntimeException("Duplicate type discriminator in subtree of " + intf);
			}
			result.add(type);
			queue.addAll(obj.getSubclasses().values());
		}
		return result;
	}
}

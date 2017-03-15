/*
 * Copyright 2016 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.output.typeScript;

import com.bluecirclesoft.open.jigen.model.JAny;
import com.bluecirclesoft.open.jigen.model.JArray;
import com.bluecirclesoft.open.jigen.model.JBoolean;
import com.bluecirclesoft.open.jigen.model.JEnum;
import com.bluecirclesoft.open.jigen.model.JMap;
import com.bluecirclesoft.open.jigen.model.JNumber;
import com.bluecirclesoft.open.jigen.model.JObject;
import com.bluecirclesoft.open.jigen.model.JSpecialization;
import com.bluecirclesoft.open.jigen.model.JString;
import com.bluecirclesoft.open.jigen.model.JTypeVariable;
import com.bluecirclesoft.open.jigen.model.JTypeVisitor;
import com.bluecirclesoft.open.jigen.model.JVoid;

/**
 * In the case of accessor functionals, a "constructor" needs to be provided to create new objects when walking down the chain.
 */
class CreateConstructorVisitor implements JTypeVisitor<String> {

	@Override
	public String visit(JObject jObject) {
		if (jObject.getNewObjectJson() == null) {
			return null;
		} else {
			return "() => { return " + jObject.getNewObjectJson() + "; }";
		}
	}

	@Override
	public String visit(JAny jAny) {
		return null;
	}

	@Override
	public String visit(JArray jArray) {
		return "() => []";
	}

	@Override
	public String visit(JBoolean jBoolean) {
		return "() => false";
	}

	@Override
	public String visit(JEnum jEnum) {
		TypeUsageProducer typeUsageProducer = new TypeUsageProducer();
		return "() => " + jEnum.accept(typeUsageProducer) + "." + jEnum.getValues().iterator().next();
	}

	@Override
	public String visit(JNumber jNumber) {
		return "() => 0";
	}

	@Override
	public String visit(JString jString) {
		return "() => ''";
	}

	@Override
	public String visit(JVoid jVoid) {
		return null;
	}

	@Override
	public String visit(JSpecialization jSpecialization) {
		return null;
	}

	@Override
	public String visit(JTypeVariable jTypeVariable) {
		return null;
	}

	@Override
	public String visit(JMap jMap) {
		return null;
	}
}

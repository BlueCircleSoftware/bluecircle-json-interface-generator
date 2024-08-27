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

package com.bluecirclesoft.open.jigen.model;

/**
 * Visitor for the different concrete TypeScript types.
 */
public interface JTypeVisitorVoid {

	void visit(JObject jObject);

	void visit(JAny jAny);

	void visit(JArray jArray);

	void visit(JBoolean jBoolean);

	void visit(JEnum jEnum);

	void visit(JNumber jNumber);

	void visit(JString jString);

	void visit(JVoid jVoid);

	void visit(JSpecialization jSpecialization);

	void visit(JTypeVariable jTypeVariable);

	void visit(JMap jMap);

	void visit(JUnionType jUnionType);

	void visit(JNull jNull);

	void visit(JWildcard jWildcard);
}

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
public interface JTypeVisitor<ReturnType> {

	ReturnType visit(JObject jObject);

	ReturnType visit(JAny jAny);

	ReturnType visit(JArray jArray);

	ReturnType visit(JBoolean jBoolean);

	ReturnType visit(JEnum jEnum);

	ReturnType visit(JNumber jNumber);

	ReturnType visit(JString jString);

	ReturnType visit(JVoid jVoid);

	ReturnType visit(JSpecialization jSpecialization);

	ReturnType visit(JTypeVariable jTypeVariable);

	ReturnType visit(JMap jMap);

	ReturnType visit(JUnionType jUnionType);

	ReturnType visit(JNull jNull);

	ReturnType visit(JWildcard jWildcard);
}

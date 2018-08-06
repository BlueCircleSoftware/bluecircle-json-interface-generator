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

package com.bluecirclesoft.open.jigen.annotations;

/**
 * Tell JIG how the actual types should be discriminated on the client side.
 */
public enum DiscriminatedBy {
	/**
	 * The method has a runtime-defined value (JIG must be able to create an instance of the object to get this value)
	 */
	RETURN_VALUE,
	/**
	 * The method will be returning the class name (JIG can use this without instantiating the class)
	 */
	CLASS_NAME,
}

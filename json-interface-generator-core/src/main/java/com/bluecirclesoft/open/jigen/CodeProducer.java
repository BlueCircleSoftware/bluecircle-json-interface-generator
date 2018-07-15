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

package com.bluecirclesoft.open.jigen;

import java.io.IOException;

import com.bluecirclesoft.open.jigen.model.Model;

/**
 * Interface for a class that takes a {@link com.bluecirclesoft.open.jigen.model.Model} and produces some code.
 */
public interface CodeProducer<T> extends ConfigurableProcessor<T> {

	/**
	 * Convert and output the given model.
	 *
	 * @param model the model
	 * @throws IOException on any I/O error
	 */
	void output(Model model) throws IOException;
}

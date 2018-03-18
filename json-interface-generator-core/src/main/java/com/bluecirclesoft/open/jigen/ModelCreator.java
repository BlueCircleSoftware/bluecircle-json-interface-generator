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

import java.util.List;

import com.bluecirclesoft.open.getopt.GetOpt;
import com.bluecirclesoft.open.jigen.model.Model;

/**
 * Interface for something that creates a {@link com.bluecirclesoft.open.jigen.model.Model}
 */
public interface ModelCreator {

	/**
	 * Add necessary command-line options for this creator to the command-line processor. It is expected that the command-line option
	 * handlers will call setters on {@code this}
	 *
	 * @param options the processor object
	 */
	void addOptions(GetOpt options);

	/**
	 * Validate whatever values were obtained from the command line.
	 *
	 * @param options the options object
	 * @param errors  a list of errors to add this object's specific errors to.
	 */
	void validateOptions(GetOpt options, List<String> errors);

	/**
	 * Create the model.
	 *
	 * @return the model
	 */
	Model createModel();
}

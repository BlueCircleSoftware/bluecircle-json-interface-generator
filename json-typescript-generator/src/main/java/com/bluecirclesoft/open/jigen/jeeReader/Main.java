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

package com.bluecirclesoft.open.jigen.jeeReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bluecirclesoft.open.getopt.GetOpt;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.output.typeScript.TypeScriptProducer;

/**
 * TODO document me
 */
public final class Main {

	private Main() {
	}

	public static void main(String[] args) throws IOException {

		JavaEEModeller modeller = new JavaEEModeller();
		TypeScriptProducer outputTypeScript = new TypeScriptProducer();

		GetOpt getOpt = GetOpt.create("jigen");
		modeller.addOptions(getOpt);
		outputTypeScript.addOptions(getOpt);
		getOpt.processParams(args);

		List<String> errors = new ArrayList<>();
		modeller.validateOptions(getOpt, errors);
		outputTypeScript.validateOptions(getOpt, errors);
		if (errors.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (String err : errors) {
				builder.append("ERROR: ").append(err).append("\n");
			}
			getOpt.usage(builder);
			System.err.println(errors.toString());
			System.exit(1);
		}

		Model model = modeller.createModel();
		outputTypeScript.output(model);
	}

}

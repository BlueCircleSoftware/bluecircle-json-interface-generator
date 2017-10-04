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

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.bluecirclesoft.open.getopt.GetOpt;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.output.typeScript.TypeScriptProducer;

/**
 * TODO document me
 */
public class Main {

	public static void main(String[] args) throws IOException {
		Options options = new Options();
		GetOpt getOpt = GetOpt.createFromReceptacle(options, Main.class);
		getOpt.processParams(args);

		StringBuilder errors = new StringBuilder();
		if (StringUtils.isBlank(options.getPackageName())) {
			errors.append("Package name to process is required.\n");
		}
		if (StringUtils.isBlank(options.getUrlPrefix())) {
			errors.append("URL prefix is required.\n");
		}
		if (StringUtils.isBlank(options.getOutputFile())) {
			errors.append("Output file is required.\n");
		}
		if (errors.length() > 0) {
			getOpt.usage(errors);
			System.err.println(errors.toString());
			System.exit(1);
		}

		JavaEEModeller modeller = new JavaEEModeller();
		String[] packages = options.getPackageName().split("[, \t]");
		Model model = modeller.createModel(options.getUrlPrefix(), packages);


		TypeScriptProducer outputTypeScript = new TypeScriptProducer(new File(options.getOutputFile()), options.getTypingsIndexPath());
		outputTypeScript.setProduceImmutables(!options.isNoImmutables());
		outputTypeScript.output(model);

	}

}

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.bluecirclesoft.open.getopt.GetOpt;
import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
public final class Main {

	private Main() {
	}

	private static void usage() {
		System.err.println("Usage:");
		System.err.println(
				"  jigen (--input|--output) <processor> <processor arg>... [(--input|--output) <processor> <processor arg>...]...");
		System.err.println("  --process <processor> instantiate a processor to process the model, passing arguments as necessary.");
		System.err.println("");
		System.err.println("You will typically want an input processor, and an output processor");
	}

	/**
	 * Take a command-line argument list, and convert it into a list of processor-specific arg lines
	 *
	 * @param args the arguments
	 * @return a list of lists, each list beginning with the specified processor
	 */
	static List<List<String>> split(String... args) {
		List<String> current = null;
		List<List<String>> result = new ArrayList<>();
		for (String arg : args) {
			switch (arg) {
				case "--input":
				case "--output":
					if (current != null) {
						result.add(current);
					}
					current = new ArrayList<>();
					current.add(arg);
					break;
				default:
					if (current == null) {
						System.err.println("Must specify a processor with --input or --output");
						System.exit(1);
					}
					current.add(arg);
					break;
			}
		}
		if (current != null) {
			result.add(current);
		}
		return result;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

		List<List<String>> argList = split(args);
		Model model = null;

		for (List<String> processorArgs : argList) {
			if (Objects.equals(processorArgs.get(0), "--input")) {
				String processorName = processorArgs.get(1);
				Object o = findClass(processorName, "Reader");
				ModelCreator modeller = (ModelCreator) o;

				GetOpt getOpt = GetOpt.create("jigen");
				modeller.addOptions(getOpt);
				getOpt.processParams(processorArgs.subList(2, processorArgs.size()));

				List<String> errors = new ArrayList<>();
				modeller.validateOptions(getOpt, errors);
				handleErrors(getOpt, errors);
				model = modeller.createModel();
			}
		}

		for (List<String> processorArgs : argList) {
			if (Objects.equals(processorArgs.get(0), "--output")) {
				String processorName = processorArgs.get(1);
				Object o = findClass(processorName, "Writer");
				CodeProducer producer = (CodeProducer) o;

				GetOpt getOpt = GetOpt.create("jigen");
				producer.addOptions(getOpt);
				getOpt.processParams(processorArgs.subList(2, processorArgs.size()));

				List<String> errors = new ArrayList<>();
				producer.validateOptions(getOpt, errors);
				handleErrors(getOpt, errors);
				producer.output(model);
			}
		}
	}

	private static Object findClass(String processorName, String className)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class creatorClass;
		if (processorName.indexOf(".") > 0) {
			creatorClass = Class.forName(processorName);
		} else {
			creatorClass = Class.forName("com.bluecirclesoft.open.jigen." + processorName + "." + className);
		}
		return creatorClass.newInstance();
	}

	private static void handleErrors(GetOpt getOpt, List<String> errors) {
		if (errors.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (String err : errors) {
				builder.append("ERROR: ").append(err).append("\n");
			}
			getOpt.usage(builder);
			System.err.println(errors.toString());
			System.exit(1);
		}
	}

}

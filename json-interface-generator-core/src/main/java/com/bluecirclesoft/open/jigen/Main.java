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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bluecirclesoft.open.jigen.model.Model;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO document me
 */
public final class Main {

	private static final String JIG_COMMON_PACKAGE = "com.bluecirclesoft.open.jigen.";


	private Main() {
	}

	private static void usage() {
		System.err.println("Usage:");
		System.err.println("  jigen (--input|--output) <processor> [(--input|--output) <processor>]... [--config <config file>]");
		System.err.println("");
		System.err.println("  (--input|--output) <processor> instantiate a processor to process the model, \n" +
				"                                 passing arguments as necessary.");
		System.err.println("");
		System.err.println("  --config <config file>         specify config file (default ./jig-config.json)");
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

		List<ModelCreator> modellers = new ArrayList<>();
		List<CodeProducer> producers = new ArrayList<>();

		File configFile = null;

		for (List<String> processorArgs : argList) {
			String argSpecifier = processorArgs.get(0);
			if (Objects.equals(argSpecifier, "--input")) {
				String processorName = processorArgs.get(1);
				Object o = findClass(processorName, "Reader");
				ModelCreator modeller = (ModelCreator) o;
				modellers.add(modeller);
			} else if (Objects.equals(argSpecifier, "--output")) {
				String processorName = processorArgs.get(1);
				Object o = findClass(processorName, "Writer");
				CodeProducer producer = (CodeProducer) o;
				producers.add(producer);
			} else if (Objects.equals(argSpecifier, "--config")) {
				configFile = new File(processorArgs.get(1));
			} else {
				System.err.println("Unknown argument: " + argSpecifier);
				usage();
				System.exit(1);
			}
		}

		// if user didn't specify a config file, check for default (or just go without config)
		if (configFile == null) {
			configFile = new File("./jig-config.json");
			if (!configFile.canRead()) {
				configFile = null;
			}
		} else {
			if (!configFile.canRead()) {
				System.err.println("Specified config file " + configFile.getAbsolutePath() + " cannot be read.");
				usage();
				System.exit(1);
			}
		}

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> config = null;
		if (configFile != null) {
			config = mapper.readValue(configFile, Map.class);
		}

		// check arguments
		List<String> errors = new ArrayList<>();
		for (ModelCreator modeller : modellers) {
			configureOneProcessor(mapper, config, errors, modeller);
		}
		for (CodeProducer producer : producers) {
			configureOneProcessor(mapper, config, errors, producer);
		}

		handleErrors(errors);

		// apply processors
		for (ModelCreator modeller : modellers) {
			model = modeller.createModel();
		}
		for (CodeProducer producer : producers) {
			producer.output(model);
		}
	}

	private static void configureOneProcessor(ObjectMapper mapper, Map<String, Object> config, List<String> errors,
	                                          ConfigurableProcessor processor) {
		String label = findConfigLabel(processor.getClass().getName());
		Class<?> optionsClass = processor.getOptionsClass();
		Object configMap = null;
		if (config != null) {
			configMap = config.get(label);
		}
		if (configMap == null) {
			configMap = new HashMap<String, Object>();
		}
		Object configObj = mapper.convertValue(configMap, optionsClass);

		processor.acceptOptions(configObj, errors);
	}

	private static String findConfigLabel(String name) {
		if (name.startsWith(JIG_COMMON_PACKAGE)) {
			// strip the JIG_COMMON_PACKAGE leader, and the .Reader or .Writer trailer
			String remainder1 = name.substring(JIG_COMMON_PACKAGE.length());
			int dotPos = remainder1.indexOf('.');
			String remainder2 = remainder1.substring(0, dotPos);
			return remainder2;
		} else {
			return name;
		}
	}

	private static Object findClass(String processorName, String className)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class creatorClass;
		if (processorName.indexOf(".") > 0) {
			creatorClass = Class.forName(processorName);
		} else {
			creatorClass = Class.forName(JIG_COMMON_PACKAGE + processorName + "." + className);
		}
		return creatorClass.newInstance();
	}

	private static void handleErrors(List<String> errors) {
		if (errors.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (String err : errors) {
				builder.append("ERROR: ").append(err).append("\n");
			}
			System.err.println(builder.toString());
			usage();
			System.exit(1);
		}
	}

}

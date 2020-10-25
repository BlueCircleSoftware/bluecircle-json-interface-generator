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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
public final class Main {

	private static final String JIG_COMMON_PACKAGE = "com.bluecirclesoft.open.jigen.";


	private Main() {
	}

	private static void usage() {
		System.err.println("Usage:");
		System.err.println("  jigen [--config <config file>]");
		System.err.println("");
		System.err.println("  --config <config file>         specify config file (default ./jig-config.yaml)");
		System.err.println("");
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

		List<ModelCreator> modellers = new ArrayList<>();
		List<CodeProducer> producers = new ArrayList<>();

		File configFile = null;

		if (args.length > 0) {
			String argSpecifier = args[0];
			if (Objects.equals(argSpecifier, "--config")) {
				configFile = new File(args[1]);
			} else {
				System.err.println("Unknown argument: " + argSpecifier);
				usage();
				System.exit(1);
			}
		}

		// if user didn't specify a config file, check for default (or just go without config)
		if (configFile == null) {
			configFile = new File("./jig-config.yaml");
		}
		if (!configFile.canRead()) {
			System.err.println("Config file " + configFile.getAbsolutePath() + " cannot be read.");
			usage();
			System.exit(1);
		}

		ConfigurationReader config = new ConfigurationReader();
		config.read(configFile);

		// check arguments
		List<String> errors = new ArrayList<>();
		for (Map.Entry<String, Object> reader : config.getReaderEntries().entrySet()) {
			String processorName = reader.getKey();
			Object o = findClass(processorName, "Reader");
			ModelCreator modeller = (ModelCreator) o;
			modellers.add(modeller);
			config.configureOneProcessor(errors, modeller, "readers", reader.getKey());
		}

		if (modellers.size() == 0) {
			errors.add("No readers specified in config");
		}

		for (Map.Entry<String, Object> reader : config.getWriterEntries().entrySet()) {
			String processorName = reader.getKey();
			Object o = findClass(processorName, "Writer");
			CodeProducer producer = (CodeProducer) o;
			producers.add(producer);
			config.configureOneProcessor(errors, producer, "writers", reader.getKey());
		}

		if (producers.size() == 0) {
			errors.add("No writers specified in config");
		}

		handleErrors(errors);

		Model model = new Model();
		// apply processors
		for (ModelCreator modeller : modellers) {
			modeller.model(model);
		}
		model.doGlobalCleanups();
		for (CodeProducer producer : producers) {
			producer.output(model);
		}
	}


	private static String findConfigLabel(String name) {
		if (name.startsWith(JIG_COMMON_PACKAGE)) {
			// strip the JIG_COMMON_PACKAGE leader, and the .Reader or .Writer trailer
			String remainder = name.substring(JIG_COMMON_PACKAGE.length());
			int dotPos = remainder.indexOf('.');
			return remainder.substring(0, dotPos);
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

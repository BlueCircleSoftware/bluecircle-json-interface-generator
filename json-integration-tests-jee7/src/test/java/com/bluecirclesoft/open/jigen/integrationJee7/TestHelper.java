/*
 * Copyright 2017 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.integrationJee7;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

/**
 * Helpers for testing
 */
public final class TestHelper {

	private static final Logger log = LoggerFactory.getLogger(TestHelper.class);

	public static class EnvPair {

		final String name;

		final String value;

		public EnvPair(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	private TestHelper() {
	}

	/**
	 * Shell out and run a command (like libc system(3))
	 *
	 * @param cmd the command to run
	 */
	public static void system(String cmd) {
		execEnv(null, "sh", "-c", cmd);
	}

	/**
	 * Run an subprocess
	 *
	 * @param environment environment variables to add, or {@code null} to keep parent environment
	 * @param cmd         the executable to invoke
	 * @param args        the arguments to pass to the executable
	 */
	public static void execEnv(EnvPair[] environment, String cmd, String... args) {
		List<String> cmdLine = new ArrayList<>();
		cmdLine.add(cmd);
		Collections.addAll(cmdLine, args);
		log.info("Executing: {} {}", cmd, Arrays.toString(args));
		ProcessExecutor pe = new ProcessExecutor();
		pe.command(cmdLine);
		pe.redirectOutput(Slf4jStream.ofCaller().asInfo());
		pe.redirectError(Slf4jStream.ofCaller().asError());
		if (environment != null) {
			for (EnvPair ep : environment) {
				pe.environment(ep.name, ep.value);
			}
		}
		try {
			ProcessResult result = pe.execute();
			if (result.getExitValue() != 0) {
				throw new RuntimeException("Process exited with error code " + result.getExitValue());
			}
		} catch (IOException | InterruptedException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

}

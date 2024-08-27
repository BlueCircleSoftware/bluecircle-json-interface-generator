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

package com.bluecirclesoft.open.jigen.integrationSpring;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO document me
 */
public class LogSetupTest {

	private static final Logger log = LoggerFactory.getLogger(LogSetupTest.class);

	private static boolean setupDone;

	public LogSetupTest() {
		setUpLogging();
	}

	public static void setUpLogging() {
		synchronized (LogSetupTest.class) {
			if (!setupDone) {
				setupDone = true;
				try {
					File target = new File("target");
					String[] list = target.list();
					if (list != null) {
						File snippet = new File("wildfly.extra.logging.properties");
						for (String tFile : list) {
							if (tFile.startsWith("wildfly-")) {
								File props = new File(target, tFile + "/standalone/configuration/logging.properties");
								log.info("Appending {} to {}", snippet, props);
								try (FileInputStream fis = new FileInputStream(snippet)) {
									try (FileOutputStream fos = new FileOutputStream(props, true)) {
										IOUtils.copy(fis, fos);
									}
								}
							}
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Test
	public void test() {
		log.info("Setting up logging");
		setUpLogging();
	}

}

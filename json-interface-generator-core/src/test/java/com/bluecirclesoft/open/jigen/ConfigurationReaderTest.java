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

package com.bluecirclesoft.open.jigen;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO document me
 */
public class ConfigurationReaderTest {

	@Test
	public void read() throws IOException {
		InputStream yamlFile = ConfigurationReaderTest.class.getResourceAsStream("ConfigTest1.yaml");
		assert yamlFile != null;
		ConfigurationReader configurationReader = new ConfigurationReader();
		configurationReader.read(yamlFile);
		Map<String, Object> result1 = configurationReader.getMap();
		Assert.assertTrue(result1.containsKey("readers"));
		Assert.assertTrue(result1.containsKey("writers"));

		Map<String, Object> readers = configurationReader.getReaderEntries();
		Assert.assertTrue(readers.containsKey("jee7"));

		List<String> errors = new ArrayList<>();
		Jee7Processor jee7Processor = new Jee7Processor();
		configurationReader.configureOneProcessor(errors, jee7Processor, "readers", "jee7");
		Jee7Options options = jee7Processor.getOptions();

		Assert.assertEquals(1, options.getPackages().size());
		Assert.assertEquals("com.bluecirclesoft", options.getPackages().get(0));
		Assert.assertEquals(true, options.isDefaultStringEnums());
	}
}
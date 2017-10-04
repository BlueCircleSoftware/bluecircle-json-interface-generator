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

package com.bluecirclesoft.open.jigen.output.typeScript;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
public class TypeScriptProducerTest {

	private static final Logger log = LoggerFactory.getLogger(TypeScriptProducerTest.class);

	@Test
	public void testJavaEEModel() throws IOException, ClassNotFoundException {
		File inFile = new File("../json-jee7-reader/JavaEEModellerTest.model.dat");
		log.info("Looking for JavaEE model in " + inFile.getCanonicalPath());
		FileInputStream in = new FileInputStream(inFile);
		ObjectInputStream objectOutputStream = new ObjectInputStream(in);
		Model model = (Model) objectOutputStream.readObject();
		objectOutputStream.close();

		TypeScriptProducer outputTypeScript = new TypeScriptProducer(new PrintWriter(System.out), "abc");
		outputTypeScript.output(model);
	}

}

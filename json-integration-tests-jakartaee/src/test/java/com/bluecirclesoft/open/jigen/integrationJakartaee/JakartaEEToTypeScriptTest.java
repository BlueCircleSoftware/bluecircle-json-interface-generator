/*
 * Copyright 2024 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.integrationJakartaee;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.jakartaee.Options;
import com.bluecirclesoft.open.jigen.jakartaee.Reader;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.typescript.OutputStructure;
import com.bluecirclesoft.open.jigen.typescript.Writer;

/**
 * Test the Java EE -> TypeScript translation.
 * <p>
 * Define some test services, deploy them in WildFly, and try to invoke them through the generated TypeScript.
 */
@RunWith(Arquillian.class)
public class JakartaEEToTypeScriptTest {

	private static final Logger log = LoggerFactory.getLogger(JakartaEEToTypeScriptTest.class);

	@Deployment(testable = false)
	public static WebArchive createDeployment() {
		try {
			log.info("Creating deployment");
			WebArchive jar = ShrinkWrap.create(WebArchive.class)
					.addPackages(true, "com.bluecirclesoft.open.jigen.integrationJakartaee")
					.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
			log.info("Created deployment: {}", jar.toString(true));
			return jar;
		} catch (Exception e) {
			log.error("Error creating deployment", e);
			throw e;
		}
	}

	@Test
	@RunAsClient
	public void runTest(@ArquillianResource URL baseUrl) throws IOException {
		// We've deployed our services to Arquillian at this point
		// Generate the TypeScript, and run the Jasmine tests

		String baseRestUrl = (baseUrl + "rest").replace("127.0.0.1", "localhost");
		log.info("Using base REST url of {}", baseRestUrl);
		doubleCheckServer(baseRestUrl);


		// Create model
		Reader modeller = new Reader();
		List<String> errors = new ArrayList<>();
		modeller.acceptOptions(makeInputOptions("com.bluecirclesoft"), errors);
		Assert.assertEquals(0, errors.size());
		Model model = new Model();
		modeller.model(model);
		model.doGlobalCleanups();

		// Create typescript
		Writer outputTypeScript = new Writer();
		outputTypeScript.acceptOptions(makeOutputOptions("target/generated-sources/jakartaeeToTypeScript"), errors);
		Assert.assertEquals(0, errors.size());
		try {
			outputTypeScript.output(model);
		} catch (Throwable t) {
			log.error("Caught exception", t);
			throw t;
		}

		// Run test cases from test browser
		TestHelper.system("npm install");
		TestHelper.system("../node_modules/.bin/webpack --bail");
		TestHelper.system("../node_modules/.bin/karma start --baseUrl " + baseRestUrl);
	}

	private static com.bluecirclesoft.open.jigen.typescript.Options makeOutputOptions(String s) {
		com.bluecirclesoft.open.jigen.typescript.Options options = new com.bluecirclesoft.open.jigen.typescript.Options();
		options.setProduceImmutables(true);
		options.setUseUnknown(true);
		options.setOutputStructure(OutputStructure.NAMESPACES);
		options.setOutputFile(s + ".ts");
		return options;
	}

	private static Options makeInputOptions(String p) {
		Options options = new Options();
		options.setPackages(Collections.singletonList(p));
		return options;
	}

	private static void doubleCheckServer(String baseRestUrl) {
		TestHelper.system("curl --fail-with-body -v " + baseRestUrl + "/testServicesString/serviceCheck");
	}


}

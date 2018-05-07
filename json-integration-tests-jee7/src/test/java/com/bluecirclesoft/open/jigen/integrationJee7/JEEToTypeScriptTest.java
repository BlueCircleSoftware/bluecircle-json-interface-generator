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
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.jee7.Reader;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.typescript.Writer;

/**
 * Test the Java EE -> TypeScript translation.
 * <p></p>
 * Define some test services, deploy them in WildFly, and try to invoke them through the generated TypeScript.
 */
@RunWith(Arquillian.class)
public class JEEToTypeScriptTest {

	private static final Logger log = LoggerFactory.getLogger(JEEToTypeScriptTest.class);

	@Deployment(testable = false)
	public static WebArchive createDeployment() {
		try {
			log.info("Creating deployment");
			WebArchive jar = ShrinkWrap.create(WebArchive.class)
					.addPackages(true, "com.bluecirclesoft.open.jigen.integrationJee7")
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
		log.info("Using base REST url of " + baseRestUrl);
//		doubleCheckServer(baseRestUrl);

		// Create model
		Reader modeller = new Reader();
		modeller.setPackageNamesString("com.bluecirclesoft");
		Model model = modeller.createModel();

		// Create typescript
		Writer outputTypeScript = new Writer();
		outputTypeScript.setOutputFile("target/generated-sources/jeeToTypeScript.ts");
		outputTypeScript.output(model);

		// Run test cases from test browser
		TestHelper.system("npm install");
		TestHelper.system("../node_modules/.bin/webpack");
		TestHelper.system("../node_modules/.bin/karma start --baseUrl " + baseRestUrl);
	}

	private void doubleCheckServer(String baseRestUrl) {
		TestHelper.system("curl " + baseRestUrl + "/testServices/serviceCheck");
	}


}
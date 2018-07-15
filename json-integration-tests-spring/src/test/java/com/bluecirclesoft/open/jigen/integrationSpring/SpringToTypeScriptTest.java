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

package com.bluecirclesoft.open.jigen.integrationSpring;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.spring.Options;
import com.bluecirclesoft.open.jigen.spring.Reader;
import com.bluecirclesoft.open.jigen.typescript.Writer;

/**
 * Test the Java EE -> TypeScript translation.
 * <p></p>
 * Define some test services, deploy them in WildFly, and try to invoke them through the generated TypeScript.
 */
//@RunWith(Arquillian.class)
public class SpringToTypeScriptTest {

	private static final Logger log = LoggerFactory.getLogger(SpringToTypeScriptTest.class);

	//	@Deployment(testable = false)
	public static WebArchive createDeployment() {
		try {
			log.info("Creating Spring deployment");
			WebArchive jar = ShrinkWrap.create(WebArchive.class)
					.addPackages(true, "com.bluecirclesoft.open.jigen.integrationSpring")
					.addAsLibraries(resolveArtifacts("org.springframework:spring-web", "org.springframework:spring-webmvc",
							"org" + ".springframework:spring-beans", "org.springframework:spring-context",
							"com.fasterxml.jackson.core:jackson-annotations", "com.fasterxml.jackson.core:jackson-databind"))
					.addAsManifestResource("Beans.xml", "Beans.xml")
					.addAsWebInfResource("WEB-INF/TestServlet-servlet.xml", "TestServlet-servlet.xml")
					.addAsWebInfResource("WEB-INF/jboss-web.xml", "jboss-web.xml")
					.setWebXML("WEB-INF/web.xml");
			log.info("Created Spring deployment: {}", jar.toString(true));
			return jar;
		} catch (Exception e) {
			log.error("Error creating deployment", e);
			throw e;
		}
	}

	public static File[] resolveArtifacts(String... artifacts) {
		Set<File> result = new HashSet<>();
		PomEquippedResolveStage mvnResolver = Maven.resolver().loadPomFromFile("pom.xml");
		for (String artifact : artifacts) {
			result.addAll(Arrays.asList(mvnResolver.resolve(artifact).withTransitivity().asFile()));
		}
		return result.toArray(new File[0]);
	}

	//	@Test
//	@RunAsClient
	public void runTest(@ArquillianResource URL baseUrl) throws IOException {
		// We've deployed our services to Arquillian at this point
		// Generate the TypeScript, and run the Jasmine tests

		String baseRestUrl = (baseUrl.toString()).replace("127.0.0.1", "localhost");
		log.info("Using base REST url of " + baseRestUrl);
//		doubleCheckServer(baseRestUrl);

		// Create model
		Reader modeller = new Reader();
		List<String> errors = new ArrayList<>();
		modeller.acceptOptions(makeInputOptions("com.bluecirclesoft"), errors);
		Assert.assertEquals(0, errors.size());
		Model model = modeller.createModel();

		// Create typescript
		Writer outputTypeScript = new Writer();
		errors = new ArrayList<>();
		outputTypeScript.acceptOptions(makeOutputOptions("target/generated-sources/springToTypeScript.ts"), errors);
		Assert.assertEquals(0, errors.size());
		outputTypeScript.output(model);

		// Run test cases from test browser
		TestHelper.system("npm install");
		TestHelper.system("../node_modules/.bin/webpack");
		TestHelper.system("../node_modules/.bin/karma start --baseUrl " + baseRestUrl);
	}

	private com.bluecirclesoft.open.jigen.typescript.Options makeOutputOptions(String s) {
		com.bluecirclesoft.open.jigen.typescript.Options options = new com.bluecirclesoft.open.jigen.typescript.Options();
		options.setOutputFile(s);
		return options;
	}

	private Options makeInputOptions(String p) {
		Options options = new Options();
		options.setPackages(Collections.singletonList(p));
		return options;
	}

	private void doubleCheckServer(String baseRestUrl) {
		TestHelper.system("curl " + baseRestUrl + "/testServices/serviceCheck");
	}


}

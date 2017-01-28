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

import com.bluecirclesoft.open.jigen.inputJackson.JacksonTypeModeller;
import com.bluecirclesoft.open.jigen.model.Model;
import org.junit.Test;
import testp1.p12.ClassB;
import testp1.p12.ClassC;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * TODO document me
 */
public class TypeScriptProducerTest {

	@Test
	public void testNamespaceCollapse() throws IOException {
		JacksonTypeModeller jacksonTypeModeller = new JacksonTypeModeller();
		Model model = new Model();
		jacksonTypeModeller.enumerateProperties(model, ClassC.class);

		TypeScriptProducer outputTypeScript = new TypeScriptProducer(new PrintWriter(System.out),"abc");
		outputTypeScript.output(model);
	}

}

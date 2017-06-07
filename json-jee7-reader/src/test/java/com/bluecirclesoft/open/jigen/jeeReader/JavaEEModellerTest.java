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

package com.bluecirclesoft.open.jigen.jeeReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Test;

import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
public class JavaEEModellerTest {

	@Test
	public void testModeller() throws IOException {

		ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);

		JavaEEModeller modeller = new JavaEEModeller();

		Model model = modeller.createModel("/", "com.bluecirclesoft");

		Assert.assertEquals(5, sizeof(model.getEndpoints()));

		// test complex endpoints
		{
			Endpoint endpoint = model.getEndpoint("com.bluecirclesoft.open.jigen.jeeReader.ComplexService.getMapSS");
			Assert.assertEquals(0, endpoint.getParameters().size());
			Assert.assertEquals("JMap[valueType=JString[]]", endpoint.getResponseBody().toString());
		}

		// Save model here, so it can be used in the typescript test
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("JavaEEModellerTest.model.dat"));
		objectOutputStream.writeObject(model);
		objectOutputStream.close();
	}

	private int sizeof(Iterable<?> iterable) {
		if (iterable instanceof Collection) {
			return ((Collection<?>) iterable).size();
		} else {
			int i = 0;
			for (Object anIterable : iterable) {
				i++;
			}
			return i;
		}
	}
}

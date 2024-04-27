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

package com.bluecirclesoft.open.jigen.jakartaee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Test;

import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
public class JakartaEEModellerTest {

	@Test
	public void testModeller() {

		ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);

		Reader modeller = new Reader();

		Options options = new Options();
		List<String> plist = new ArrayList<>();
		plist.add("com.bluecirclesoft");
		options.setPackages(plist);

		List<String> errors = new ArrayList<>();
		modeller.acceptOptions(options, errors);
		Assert.assertEquals(0, errors.size());
		Model model = new Model();
		modeller.model(model);
		model.doGlobalCleanups();

		Assert.assertEquals(5, sizeof(model.getEndpoints()));

		// test complex endpoints
		{
			Endpoint endpoint = model.getEndpoint("com.bluecirclesoft.open.jigen.jakartaee.ComplexService.getMapSS");
			Assert.assertEquals(0, endpoint.getParameters().size());
			Assert.assertEquals("JMap[valueType=JString[]]", endpoint.getResponseBody().toString());
		}
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

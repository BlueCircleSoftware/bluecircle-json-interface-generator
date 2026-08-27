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

package com.bluecirclesoft.open.jigen.jee7;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Test;

import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.model.HttpMethod;

import javax.ws.rs.core.MediaType;

/**
 * TODO document me
 */
public class JavaEEModellerTest {

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

		Assert.assertEquals(17, sizeof(model.getEndpoints()));

		// test complex endpoints
		{
			Endpoint endpoint = model.getEndpoint("com.bluecirclesoft.open.jigen.jee7.ComplexService.getMapSS");
			Assert.assertEquals(0, endpoint.getParameters().size());
			Assert.assertEquals("JMap[valueType=JString[]]", endpoint.getResponseBody().toString());
		}

		Endpoint defaultJson = model.getEndpoint("com.bluecirclesoft.open.jigen.jee7.ClassLevelJsonService.defaultJson");
		Assert.assertEquals(MediaType.APPLICATION_JSON, defaultJson.getProduces());
		Assert.assertTrue(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.ClassLevelJsonService.jsonWithParams"));
		Assert.assertTrue(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.ClassLevelJsonService.vendorJson"));
		Assert.assertFalse(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.ClassLevelJsonService.textOverride"));
		Assert.assertFalse(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.ClassLevelTextService.defaultText"));
		Assert.assertTrue(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.ClassLevelTextService.jsonOverride"));

		Endpoint jsonParamConsume = model.getEndpoint("com.bluecirclesoft.open.jigen.jee7.ConsumesService.consumeJsonParam");
		Assert.assertEquals("application/json; charset=UTF-8", jsonParamConsume.getConsumes());
		Endpoint jsonMultiConsume = model.getEndpoint("com.bluecirclesoft.open.jigen.jee7.ConsumesService.consumeJsonMulti");
		Assert.assertEquals(MediaType.APPLICATION_JSON, jsonMultiConsume.getConsumes());

		Assert.assertTrue(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.OverloadedService.find__int"));
		Assert.assertTrue(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.OverloadedService.find__java_lang_String"));

		Assert.assertFalse(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.BeanParamService.skip"));
		Assert.assertFalse(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.InvalidService.invalid"));

		Endpoint patchy = model.getEndpoint("com.bluecirclesoft.open.jigen.jee7.CustomHttpMethodService.patchy");
		Assert.assertEquals(HttpMethod.PATCH, patchy.getMethod());
		Endpoint optionsEndpoint = model.getEndpoint("com.bluecirclesoft.open.jigen.jee7.CustomHttpMethodService.options");
		Assert.assertEquals(HttpMethod.OPTIONS, optionsEndpoint.getMethod());
	}

	@Test
	public void testConfiguredPackageExcludesSiblingEndpoints() {
		Reader modeller = new Reader();

		Options options = new Options();
		options.setPackages(List.of("com.bluecirclesoft.open.jigen.jee7.included"));
		List<String> errors = new ArrayList<>();
		modeller.acceptOptions(options, errors);
		Assert.assertEquals(0, errors.size());

		Model model = new Model();
		modeller.model(model);
		model.doGlobalCleanups();

		Assert.assertEquals(1, sizeof(model.getEndpoints()));
		Assert.assertNotNull(model.getEndpoint("com.bluecirclesoft.open.jigen.jee7.included.IncludedService.value"));
		Assert.assertFalse(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.excluded.ExcludedService.value"));
	}

	@Test
	public void testExplicitlyExcludedClassIsNotAnEndpoint() {
		Reader modeller = new Reader();

		Options options = new Options();
		options.setPackages(List.of("com.bluecirclesoft.open.jigen.jee7"));
		options.setExcludedClasses(List.of("com.bluecirclesoft.open.jigen.jee7.excluded.ExcludedService"));
		List<String> errors = new ArrayList<>();
		modeller.acceptOptions(options, errors);
		Assert.assertEquals(0, errors.size());

		Model model = new Model();
		modeller.model(model);
		model.doGlobalCleanups();

		Assert.assertEquals(16, sizeof(model.getEndpoints()));
		Assert.assertNotNull(model.getEndpoint("com.bluecirclesoft.open.jigen.jee7.included.IncludedService.value"));
		Assert.assertFalse(hasEndpoint(model, "com.bluecirclesoft.open.jigen.jee7.excluded.ExcludedService.value"));
	}

	private static int sizeof(Iterable<?> iterable) {
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

	private static boolean hasEndpoint(Model model, String name) {
		for (Endpoint endpoint : model.getEndpoints()) {
			if (name.equals(endpoint.getId())) {
				return true;
			}
		}
		return false;
	}
}

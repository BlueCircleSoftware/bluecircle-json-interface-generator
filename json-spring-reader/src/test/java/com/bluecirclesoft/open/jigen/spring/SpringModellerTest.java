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

package com.bluecirclesoft.open.jigen.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Test;

import com.bluecirclesoft.open.jigen.model.Endpoint;
import com.bluecirclesoft.open.jigen.model.EndpointParameter;
import com.bluecirclesoft.open.jigen.model.HttpMethod;
import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
public class SpringModellerTest {

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

		Assert.assertEquals(20, sizeof(model.getEndpoints()));

		// test complex endpoints
		{
			Endpoint endpoint = model.getEndpoint("com.bluecirclesoft.open.jigen.spring.ComplexService.getMapSS");
			Assert.assertEquals(0, endpoint.getParameters().size());
			Assert.assertEquals("JMap[valueType=JString[]]", endpoint.getResponseBody().toString());
		}

		// overloaded methods should both be present
		Assert.assertNotNull(findEndpoint(model, "/overload/one", HttpMethod.GET));
		Assert.assertNotNull(findEndpoint(model, "/overload/two", HttpMethod.GET));

		// no explicit method should default to all jsonnable methods
		Assert.assertNotNull(findEndpoint(model, "/any/ping", HttpMethod.GET));
		Assert.assertNotNull(findEndpoint(model, "/any/ping", HttpMethod.POST));
		Endpoint pingEndpoint = findEndpoint(model, "/any/ping", HttpMethod.GET);
		Assert.assertNull(pingEndpoint.getProduces());

		// multiple path variants should create multiple endpoints
		Assert.assertNotNull(findEndpoint(model, "/multiA/one", HttpMethod.GET));
		Assert.assertNotNull(findEndpoint(model, "/multiA/two", HttpMethod.GET));
		Assert.assertNotNull(findEndpoint(model, "/multiB/one", HttpMethod.GET));
		Assert.assertNotNull(findEndpoint(model, "/multiB/two", HttpMethod.GET));

		// media type parsing should allow json variants
		Endpoint charsetEndpoint = findEndpoint(model, "/media/jsonCharset", HttpMethod.GET);
		Assert.assertEquals("application/json", charsetEndpoint.getProduces());
		Endpoint vendorEndpoint = findEndpoint(model, "/media/vendor", HttpMethod.POST);
		Assert.assertEquals("application/vnd.test+json", vendorEndpoint.getConsumes());

		// implicit @PathVariable names should be inferred from the path template
		Endpoint pathVarEndpoint = findEndpoint(model, "/pv/{id}/{name}", HttpMethod.GET);
		List<String> pathParamNames = new ArrayList<>();
		for (EndpointParameter param : pathVarEndpoint.getParameters()) {
			if (param.getNetworkType() == EndpointParameter.NetworkType.PATH) {
				pathParamNames.add(param.getNetworkName());
			}
		}
		Assert.assertEquals(2, pathParamNames.size());
		Assert.assertTrue(pathParamNames.contains("id"));
		Assert.assertTrue(pathParamNames.contains("name"));

		// unannotated parameters should not be dropped
		Endpoint unannotEndpoint = findEndpoint(model, "/unannot/submit", HttpMethod.POST);
		Assert.assertEquals(1, unannotEndpoint.getParameters().size());
		EndpointParameter param = unannotEndpoint.getParameters().get(0);
		Assert.assertEquals(EndpointParameter.NetworkType.FORM, param.getNetworkType());
		Assert.assertTrue(param.getNetworkName() != null && !param.getNetworkName().isEmpty());
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

	private static Endpoint findEndpoint(Model model, String pathTemplate, HttpMethod method) {
		for (Endpoint endpoint : model.getEndpoints()) {
			if (pathTemplate.equals(endpoint.getPathTemplate()) && method == endpoint.getMethod()) {
				return endpoint;
			}
		}
		return null;
	}
}

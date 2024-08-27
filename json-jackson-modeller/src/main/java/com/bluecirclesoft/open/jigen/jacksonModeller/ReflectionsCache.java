/*
 * Copyright 2019 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.jacksonModeller;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache to hold on to Reflections scanners, since they can be very time-consuming to build (see {@link Reflections})
 */
class ReflectionsCache {

	private static final Logger logger = LoggerFactory.getLogger(ReflectionsCache.class);

	private final Map<Set<String>, Reflections> cache = new HashMap<>();

	Reflections getReflections(String[] packagesToScan) {
		Set<String> packages = new HashSet<>();
		Collections.addAll(packages, packagesToScan);
		if (cache.containsKey(packages)) {
			return cache.get(packages);
		} else {
			logger.info("Creating new Reflections scanner for package set {}", packages);

			// doing this map rigmarole to avoid using URL.equals()
			Map<URI, URL> urls = new HashMap<>();
			for (String p : packagesToScan) {
				Collection<URL> urlsIn = ClasspathHelper.forPackage(p);
				for (URL urlIn : urlsIn) {
					try {
						urls.put(urlIn.toURI(), urlIn);
					} catch (URISyntaxException e) {
						throw new RuntimeException("Problem converting classpath URL to URI", e);
					}
				}
			}
			Reflections subclassFinder = new Reflections(new ConfigurationBuilder().setUrls(urls.values()).setScanners(Scanners.SubTypes));
			cache.put(packages, subclassFinder);
			return subclassFinder;
		}
	}
}

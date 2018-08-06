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
 *
 */

package com.bluecirclesoft.open.jigen.jacksonModeller;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO document me
 */
public class ReflectionsCache {

	private static final Logger logger = LoggerFactory.getLogger(ReflectionsCache.class);

	private final Map<Set<String>, Reflections> cache = new HashMap<>();

	Reflections getReflections(String[] packagesToScan) {
		Set<String> packages = new HashSet<>();
		Collections.addAll(packages, packagesToScan);
		if (cache.containsKey(packages)) {
			return cache.get(packages);
		} else {
			logger.info("Creating new Reflections scanner for package set " + packages);
			Set<URL> urls = new HashSet<>();
			for (String p : packagesToScan) {
				urls.addAll(ClasspathHelper.forPackage(p));
			}
			Reflections subclassFinder = new Reflections(new ConfigurationBuilder().setUrls(urls).setScanners(new SubTypesScanner()));
			cache.put(packages, subclassFinder);
			return subclassFinder;
		}
	}
}

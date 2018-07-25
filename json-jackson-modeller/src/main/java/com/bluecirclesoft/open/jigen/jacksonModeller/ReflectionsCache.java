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

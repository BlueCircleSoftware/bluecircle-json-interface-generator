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

package com.bluecirclesoft.open.jigen;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassOverrideHandler {

	private static final Logger log = LoggerFactory.getLogger(ClassOverrideHandler.class);

	private Map<Class, Class> overrideClasses = new HashMap<>();

	public void ingestOverrides(String classOverrides) {
		try {
			for (String override : Regexes.COMMA_SEPARATOR.split(classOverrides)) {
				String[] cl = Regexes.EQUALS_SEPARATOR.split(override);
				if (cl.length == 0) {
					continue;
				}
				if (cl.length != 2) {
					throw new RuntimeException("Bad override spec " + override + " in " + classOverrides);
				}
				Class fromClass = Class.forName(cl[0]);
				Class toClass = Class.forName(cl[1]);
				if (overrideClasses.containsKey(fromClass)) {
					log.warn("Duplicate class override: {}", fromClass);
				}
				overrideClasses.put(fromClass, toClass);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unknown class processing overrides", e);
		}
	}


	public boolean containsKey(Class cl) {
		return overrideClasses.containsKey(cl);
	}

	public Class get(Class cl) {
		return overrideClasses.get(cl);
	}
}

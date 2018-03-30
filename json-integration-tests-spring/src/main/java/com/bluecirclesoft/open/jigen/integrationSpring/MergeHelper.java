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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For testing, merge TestDTOs
 */
public final class MergeHelper {

	private static final Logger log = LoggerFactory.getLogger(MergeHelper.class);

	private MergeHelper() {
	}

	/**
	 * Merge test DTOs
	 *
	 * @param maps DTOs or strings
	 * @return a new resulting DTO
	 */
	public static TestDto merge(Object... maps) {
		TestDto result = new TestDto();
		for (Object m : maps) {
			log.info("Merging: {}", m);
			if (m == null) {
				// do nothing
			} else if (m instanceof String) {
				result.appendAll((String) m);
			} else if (m instanceof TestDto) {
				result.append((TestDto) m);
			} else {
				throw new RuntimeException("Unhandled type " + m.getClass().getName() + " (" + m + ")");
			}
		}
		log.info("Returning result: {}", result);
		return result;
	}
}

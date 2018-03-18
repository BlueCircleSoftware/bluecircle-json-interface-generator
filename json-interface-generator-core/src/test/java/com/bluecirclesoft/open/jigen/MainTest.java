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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO document me
 */
public class MainTest {

	@Test
	public void testSplit() {
		List<List<String>> result = Main.split("--input a --input d e --output g h i".split(" "));
		Assert.assertEquals(3, result.size());
		assertEquals(2, result.get(0).size());
		assertEquals(3, result.get(1).size());
		assertEquals(4, result.get(2).size());
	}

}
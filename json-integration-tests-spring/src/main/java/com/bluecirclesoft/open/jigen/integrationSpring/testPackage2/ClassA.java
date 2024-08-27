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

package com.bluecirclesoft.open.jigen.integrationSpring.testPackage2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO document me
 */
@Setter
@Getter
public class ClassA {

	private int prop1;

	private String prop2;

	private List<String> prop3 = new ArrayList<>();

	private EnumA prop4;

	private List<EnumA> prop5 = new ArrayList<>();

	private Set<Integer> prop6 = new HashSet<>();

	private Map<String, String> prop7 = new HashMap<>();

	private Map<Integer, String> prop8 = new HashMap<>();

	private Map<Map<String, String>, Map<Integer, Integer>> prop9 = new HashMap<>();

	private Map<?, ?> prop10 = new HashMap<>();

	private EnumB prop11;

}

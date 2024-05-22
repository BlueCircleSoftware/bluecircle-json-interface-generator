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
import java.util.List;

import com.bluecirclesoft.open.jigen.ClassSubstitution;
import lombok.Getter;
import lombok.Setter;

/**
 * Options for starting a Jakarta EE reader (from YAML file)
 */
@Setter
@Getter
public class Options {

	private List<String> packages = new ArrayList<>();

	private boolean defaultStringEnums = false;

	private List<ClassSubstitution> classSubstitutions = new ArrayList<>();

	private boolean includeSubclasses = true;

	private String urlPrefix = "";
}

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

package com.bluecirclesoft.open.jigen.jee7;

import java.util.ArrayList;
import java.util.List;

import com.bluecirclesoft.open.jigen.ClassSubstitution;

/**
 * Options for starting a JEE7 reader (from YAML file)
 */
public class Options {

	private List<String> packages = new ArrayList<>();

	private boolean defaultStringEnums = false;

	private List<ClassSubstitution> classSubstitutions = new ArrayList<>();

	private boolean includeSubclasses = true;

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	public boolean isDefaultStringEnums() {
		return defaultStringEnums;
	}

	public void setDefaultStringEnums(boolean defaultStringEnums) {
		this.defaultStringEnums = defaultStringEnums;
	}

	public List<ClassSubstitution> getClassSubstitutions() {
		return classSubstitutions;
	}

	public void setClassSubstitutions(List<ClassSubstitution> classSubstitutions) {
		this.classSubstitutions = classSubstitutions;
	}

	public boolean isIncludeSubclasses() {
		return includeSubclasses;
	}

	public void setIncludeSubclasses(boolean includeSubclasses) {
		this.includeSubclasses = includeSubclasses;
	}
}

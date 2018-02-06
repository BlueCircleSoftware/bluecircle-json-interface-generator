/*
 * Copyright 2015 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.jeeReader;

import com.bluecirclesoft.open.getopt.ByArgument;
import com.bluecirclesoft.open.getopt.ByFlag;

/**
 * TODO document me
 */
public class Options {

	@ByArgument(longOpt = "package", mnemonic = "<package>", documentation = "The java packages to search")
	private String packageName;

	@ByArgument(longOpt = "url-prefix", mnemonic = "<prefix>", documentation = "The URL prefix to produce")
	private String urlPrefix;

	@ByArgument(longOpt = "output-file", mnemonic = "<file>", documentation = "The TypeScript file to generate (path will be created if " +
			"necessary")
	private String outputFile;

	@ByFlag(longOpt = "strip-common-packages", documentation = "Strip any common leading packages from all produced classes (default " +
			"false)")
	private boolean stripCommonPackages;

	@ByFlag(longOpt = "no-immutables", documentation = "Do not produce immutable classes for interfaces")
	private boolean noImmutables = false;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public boolean isStripCommonPackages() {
		return stripCommonPackages;
	}

	public void setStripCommonPackages(boolean stripCommonPackages) {
		this.stripCommonPackages = stripCommonPackages;
	}

	public boolean isNoImmutables() {
		return noImmutables;
	}

	public void setNoImmutables(boolean noImmutables) {
		this.noImmutables = noImmutables;
	}
}

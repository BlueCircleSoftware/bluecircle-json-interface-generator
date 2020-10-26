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

package com.bluecirclesoft.open.jigen.typescript;

/**
 * TODO document me
 */
public class Options {

	private String outputFile;

	private boolean stripCommonPackages;

	private boolean produceImmutables;

	private String immutableSuffix = "$Imm";

	private boolean nullIsUndefined;

	private boolean useUnknown = true;

	private OutputStructure outputStructure = OutputStructure.FILES_IN_ONE_FOLDER;

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

	public boolean isProduceImmutables() {
		return produceImmutables;
	}

	public void setProduceImmutables(boolean produceImmutables) {
		this.produceImmutables = produceImmutables;
	}

	public boolean isNullIsUndefined() {
		return nullIsUndefined;
	}

	public void setNullIsUndefined(boolean nullIsUndefined) {
		this.nullIsUndefined = nullIsUndefined;
	}

	public boolean isUseUnknown() {
		return useUnknown;
	}

	public void setUseUnknown(boolean useUnknown) {
		this.useUnknown = useUnknown;
	}

	public String getImmutableSuffix() {
		return immutableSuffix;
	}

	public void setImmutableSuffix(String immutableSuffix) {
		this.immutableSuffix = immutableSuffix;
	}

	public OutputStructure getOutputStructure() {
		return outputStructure;
	}

	public void setOutputStructure(OutputStructure outputStructure) {
		this.outputStructure = outputStructure;
	}
}

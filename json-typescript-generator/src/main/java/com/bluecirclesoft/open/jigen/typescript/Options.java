package com.bluecirclesoft.open.jigen.typescript;

/**
 * TODO document me
 */
public class Options {

	private String outputFile;

	private boolean stripCommonPackages;

	private boolean produceImmutables;

	private boolean nullIsUndefined;

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
}

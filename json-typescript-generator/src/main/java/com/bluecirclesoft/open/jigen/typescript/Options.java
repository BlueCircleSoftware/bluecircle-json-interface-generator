package com.bluecirclesoft.open.jigen.typescript;

/**
 * TODO document me
 */
public class Options {

	private String outputFile;

	private boolean stripCommonPackages;

	private boolean produceImmutables;

	private boolean nullIsUndefined;

	private boolean useUnknown;

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
}

package com.bluecirclesoft.open.jigen.jeeReader;

import com.bluecirclesoft.open.getopt.ByArgument;

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
}

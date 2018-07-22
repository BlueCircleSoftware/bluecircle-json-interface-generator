package com.bluecirclesoft.open.jigen;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO document me
 */
public class Jee7Options {

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

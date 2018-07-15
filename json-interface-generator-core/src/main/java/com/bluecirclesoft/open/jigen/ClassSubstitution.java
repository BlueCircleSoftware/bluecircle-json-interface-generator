package com.bluecirclesoft.open.jigen;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO document me
 */
public class ClassSubstitution {

	@JsonProperty(required = true)
	private String ifSeen;

	@JsonProperty(required = true)
	private String replaceWith;

	public String getIfSeen() {
		return ifSeen;
	}

	public void setIfSeen(String ifSeen) {
		this.ifSeen = ifSeen;
	}

	public String getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(String replaceWith) {
		this.replaceWith = replaceWith;
	}
}

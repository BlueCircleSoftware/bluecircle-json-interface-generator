package com.bluecirclesoft.open.jigen.integrationJee7.typeVar;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class that has a property marked "required", but when we try to create the new object JSON, the value is null (contradicting the
 * 'required', of course).
 */
public class RequiredNull {

	@JsonProperty(required = true)
	private String x;

	public String getX() {
		return x;
	}
}

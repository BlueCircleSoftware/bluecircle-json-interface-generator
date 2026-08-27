package com.bluecirclesoft.open.jigen.spring.included;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Test endpoint that must be retained when its package is explicitly configured. */
@RequestMapping(path = "/included")
public class IncludedService {

	/** Provides a simple JSON endpoint for reader filtering tests. */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public String value() {
		return "included";
	}
}

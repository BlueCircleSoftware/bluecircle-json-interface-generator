package com.bluecirclesoft.open.jigen.spring.excluded;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Test endpoint that must be omitted when it is outside the configured package or explicitly excluded. */
@RequestMapping(path = "/excluded")
public class ExcludedService {

	/** Provides a simple JSON endpoint for reader filtering tests. */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public String value() {
		return "excluded";
	}
}

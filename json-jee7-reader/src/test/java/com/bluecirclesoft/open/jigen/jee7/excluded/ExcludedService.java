package com.bluecirclesoft.open.jigen.jee7.excluded;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/** Test endpoint that must be omitted when it is outside the configured package or explicitly excluded. */
@Path("/excluded")
@Produces(MediaType.APPLICATION_JSON)
public class ExcludedService {

	/** Provides a simple JSON endpoint for reader filtering tests. */
	@GET
	public String value() {
		return "excluded";
	}
}

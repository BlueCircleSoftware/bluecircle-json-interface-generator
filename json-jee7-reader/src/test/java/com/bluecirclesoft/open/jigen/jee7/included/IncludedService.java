package com.bluecirclesoft.open.jigen.jee7.included;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/** Test endpoint that must be retained when its package is explicitly configured. */
@Path("/included")
@Produces(MediaType.APPLICATION_JSON)
public class IncludedService {

	/** Provides a simple JSON endpoint for reader filtering tests. */
	@GET
	public String value() {
		return "included";
	}
}

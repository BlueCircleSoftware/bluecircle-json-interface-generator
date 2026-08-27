package com.bluecirclesoft.open.jigen.jakartaee.included;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/included")
@Produces(MediaType.APPLICATION_JSON)
public class IncludedService {

	@GET
	public String value() {
		return "included";
	}
}

package com.bluecirclesoft.open.jigen.jakartaee.excluded;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/excluded")
@Produces(MediaType.APPLICATION_JSON)
public class ExcludedService {

	@GET
	public String value() {
		return "excluded";
	}
}

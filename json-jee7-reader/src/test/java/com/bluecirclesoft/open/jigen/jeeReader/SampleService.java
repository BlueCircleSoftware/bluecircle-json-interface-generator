package com.bluecirclesoft.open.jigen.jeeReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * TODO document me
 */
@Path("/a")
public class SampleService {

	@Path("/getPerson")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Person getPersonById(@QueryParam("id") int id) {
		return null;
	}


	@Path("/savePerson")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public int savePersonById(Person person) {
		return 3;
	}

}

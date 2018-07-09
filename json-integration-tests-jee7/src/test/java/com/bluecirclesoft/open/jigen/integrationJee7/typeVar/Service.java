package com.bluecirclesoft.open.jigen.integrationJee7.typeVar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * TODO document me
 */
@Path("/x")
public class Service {

	@GET
	@Path("/get1")
	@Consumes("application/json")
	@Produces("application/json")
	public Generic1<ABase> get1() {
		return new Generic1<>();
	}

	@GET
	@Path("/get2")
	@Consumes("application/json")
	@Produces("application/json")
	public Generic2<BBase> get2() {
		return new Generic2<>();
	}

	@GET
	@Path("/getRequiredNull")
	@Consumes("application/json")
	@Produces("application/json")
	public RequiredNull getRequiredNull() {
		return new RequiredNull();
	}

}

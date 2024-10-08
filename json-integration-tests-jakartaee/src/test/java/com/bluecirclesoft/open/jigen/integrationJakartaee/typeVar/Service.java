/*
 * Copyright 2024 Blue Circle Software, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.bluecirclesoft.open.jigen.integrationJakartaee.typeVar;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

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

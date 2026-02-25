/*
 * Copyright 2026 Blue Circle Software, LLC
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
 */

package com.bluecirclesoft.open.jigen.jee7;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/consumes")
@Produces(MediaType.APPLICATION_JSON)
public class ConsumesService {

	@POST
	@Path("/jsonParam")
	@Consumes("application/json; charset=UTF-8")
	public Person consumeJsonParam(Person person) {
		return person;
	}

	@POST
	@Path("/jsonMulti")
	@Consumes({"application/vnd.api+json", MediaType.APPLICATION_JSON})
	public Person consumeJsonMulti(Person person) {
		return person;
	}
}

package org.meveo.api.rest.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.account.GetAccessResponse;
import org.meveo.api.dto.response.account.GetListAccessResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/access")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface AccessRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(AccessDto postData);

	@PUT
	@Path("/")
	ActionStatus update(AccessDto postData);

	/**
	 * Search for a user account with a given code.
	 * 
	 * @param userAccountCode
	 * @return
	 */
	@GET
	@Path("/")
	GetAccessResponse find(@QueryParam("accessId") Long accessId);

	@DELETE
	@Path("/{accessId}")
	ActionStatus remove(@PathParam("accessId") Long accessId);

	/**
	 * List accesses given a subscription code.
	 * 
	 * @param subscriptionCode
	 * @return
	 */
	@GET
	@Path("/list")
	GetListAccessResponse list(@QueryParam("subscriptionCode") String subscriptionCode);

}

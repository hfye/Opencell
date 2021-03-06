package org.meveo.api.rest.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/accountOperation")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface AccountOperationRs extends IBaseRs {

    /**
     * Create a new account operation
     * 
     * @param postData The account operation's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(AccountOperationDto postData);

    /**
     * List of account operations
     * 
     * @param customerAccountCode The customer account's code
     * @return A list of account operations
     */
    @GET
    @Path("/list")
    AccountOperationsResponseDto list(@QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * Match operations
     * 
     * @param postData The matching operation's data
     * @return Request processing status
     */
    @POST
    @Path("/matchOperations")
    ActionStatus matchOperations(MatchOperationRequestDto postData);

    /**
     * Unmatching operations
     * 
     * @param postData The unmatching operations data
     * @return Request processing status
     */
    @POST
    @Path("/unMatchingOperations")
    ActionStatus unMatchingOperations(UnMatchingOperationRequestDto postData);

    /**
     * Add a new litigation
     * 
     * @param postData The litigation's data
     * @return Request processing status
     */
    @POST
    @Path("/addLitigation")
    ActionStatus addLitigation(LitigationRequestDto postData);

    /**
     * Cancel a litigation
     * 
     * @param postData The litigation's data
     * @return Request processing status
     */
    @POST
    @Path("/cancelLitigation")
    ActionStatus cancelLitigation(LitigationRequestDto postData);

}

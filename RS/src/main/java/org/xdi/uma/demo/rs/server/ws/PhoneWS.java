package org.xdi.uma.demo.rs.server.ws;

import org.apache.log4j.Logger;
import org.xdi.oxd.rs.protect.Jackson;
import org.xdi.uma.demo.common.gwt.Phones;
import org.xdi.uma.demo.common.gwt.RsResponse;
import org.xdi.uma.demo.common.gwt.Status;
import org.xdi.uma.demo.rs.server.PhoneService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2013
 */

@Path("/phone")
public class PhoneWS {

    private static final Logger LOG = Logger.getLogger(PhoneWS.class);

    public static final Response INTERNAL_ERROR_RESPONSE = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    @PUT
    @Path("{phone}")
    @Produces({"application/json"})
    public Response add(@PathParam("phone") String p_phone) {
        LOG.debug(String.format("Try to create/add phone... (phone:%s)", p_phone));
        try {
            final Status status;
            if (PhoneService.getInstance().add(p_phone)) {
                LOG.trace("Phone is successfully added, phone: " + p_phone);
                status = Status.CREATED;
            } else {
                LOG.trace("Failed to add phone, phone: " + p_phone);
                status = Status.FAILED;
            }

            return Response.ok().entity(Jackson.asJsonSilently(new RsResponse(status))).build();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return INTERNAL_ERROR_RESPONSE;
    }

    @DELETE
    @Path("{phone}")
    @Produces({"application/json"})
    public Response delete(@PathParam("phone") String p_phone) {
        LOG.debug(String.format("Try to remove phone... (phone:%s)", p_phone));
        try {

            final PhoneService phoneService = PhoneService.getInstance();
            if (phoneService.getPhoneList().contains(p_phone)) {
                final Status status;
                if (phoneService.remove(p_phone)) {
                    LOG.trace("Phone is successfully remove, phone: " + p_phone);
                    status = Status.DELETED;
                } else {
                    LOG.trace("Failed to remove phone, phone: " + p_phone);
                    status = Status.FAILED;
                }
                return Response.ok().entity(Jackson.asJsonSilently(new RsResponse(status))).build();
            } else {
                // phone is not in list, produce NOT_FOUND response
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return INTERNAL_ERROR_RESPONSE;
    }

    @GET
    @Produces({"application/json"})
    public Response getList() {
        try {
            final String entity = Jackson.asJsonSilently(new Phones(PhoneService.getInstance().getPhoneList()));
            LOG.debug("Returned json: " + entity);
            return Response.ok(entity).build();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return INTERNAL_ERROR_RESPONSE;
    }
}

package org.xdi.uma.demo.rs.server.ws;

import org.apache.log4j.Logger;
import org.xdi.oxauth.model.uma.RegisterPermissionRequest;
import org.xdi.oxauth.model.uma.RptIntrospectionResponse;
import org.xdi.uma.demo.common.gwt.Phones;
import org.xdi.uma.demo.common.gwt.RsResponse;
import org.xdi.uma.demo.common.gwt.Status;
import org.xdi.uma.demo.common.server.CommonUtils;
import org.xdi.uma.demo.rs.server.PermissionService;
import org.xdi.uma.demo.rs.server.PhoneService;
import org.xdi.uma.demo.rs.server.ResourceRegistry;
import org.xdi.uma.demo.rs.server.Utils;
import org.xdi.uma.demo.rs.shared.Resource;
import org.xdi.uma.demo.rs.shared.ResourceType;
import org.xdi.uma.demo.rs.shared.ScopeType;
import org.xdi.util.Pair;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

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
    public Response add(@Context HttpServletRequest p_request, @PathParam("phone") String p_phone) {
        LOG.debug(String.format("Try to create/add phone... (phone:%s)", p_phone));
        try {
            final Pair<Boolean, Response> resultPair = hasEnoughPermissions(p_request, Arrays.asList(ScopeType.ADD, ScopeType.ALL));
            if (resultPair.getFirst()) {
                final Status status;
                if (PhoneService.getInstance().add(p_phone)) {
                    LOG.trace("Phone is successfully added, phone: " + p_phone);
                    status = Status.CREATED;
                } else {
                    LOG.trace("Failed to add phone, phone: " + p_phone);
                    status = Status.FAILED;
                }

                return Response.ok().entity(CommonUtils.asJsonSilently(new RsResponse(status))).build();
            } else {
                LOG.debug("RPT doesn't have enough permissions, access FORBIDDEN. Returns HTTP 403 (Forbidden).");
                return resultPair.getSecond();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return INTERNAL_ERROR_RESPONSE;
    }

    @DELETE
    @Path("{phone}")
    @Produces({"application/json"})
    public Response delete(@Context HttpServletRequest p_request, @PathParam("phone") String p_phone) {
        LOG.debug(String.format("Try to remove phone... (phone:%s)", p_phone));
        try {

            final Pair<Boolean, Response> resultPair = hasEnoughPermissions(p_request, Arrays.asList(ScopeType.REMOVE, ScopeType.ALL));
            if (resultPair.getFirst()) {
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
                    return Response.ok().entity(CommonUtils.asJsonSilently(new RsResponse(status))).build();
                } else {
                    // phone is not in list, produce NOT_FOUND response
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } else {
                LOG.debug("RPT doesn't have enough permissions, access FORBIDDEN. Returns HTTP 403 (Forbidden).");
                return resultPair.getSecond();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return INTERNAL_ERROR_RESPONSE;
    }

    @GET
    @Produces({"application/json"})
    public Response getList(@Context HttpServletRequest p_request) {
        try {
            LOG.debug(Utils.createMsg(ScopeType.VIEW, ResourceType.PHONE));

            final Pair<Boolean, Response> resultPair = hasEnoughPermissions(p_request, Arrays.asList(ScopeType.VIEW, ScopeType.ALL));

            if (resultPair.getFirst()) {
                LOG.debug("RPT has enough permissions, access GRANTED. Returns HTTP 200 (OK).");

                final String entity = CommonUtils.asJsonSilently(new Phones(PhoneService.getInstance().getPhoneList()));

                LOG.debug("Returned json: " + entity);
                return Response.ok(entity).build();
            } else {
                LOG.debug("RPT doesn't have enough permissions, access FORBIDDEN. Returns HTTP 403 (Forbidden).");
                return resultPair.getSecond();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return INTERNAL_ERROR_RESPONSE;
    }

    private Pair<Boolean, Response> hasEnoughPermissions(HttpServletRequest p_request, List<ScopeType> p_scopes) {
        final PermissionService permissionService = PermissionService.getInstance();
        final String resourceId = getResource().getId();
        final RptIntrospectionResponse status = Utils.extract(p_request);
        final List<RegisterPermissionRequest> permissions = status.getPermissions();
        return permissionService.hasEnoughPermissionsWithTicketRegistration(permissions, resourceId, p_scopes);
    }

    private Resource getResource() {
        return ResourceRegistry.getInstance().getResourceId(ResourceType.PHONE);
    }
}

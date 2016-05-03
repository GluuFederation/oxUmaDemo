package org.xdi.uma.demo.rs.server.ws;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.xdi.oxauth.model.uma.ScopeDescription;
import org.xdi.uma.demo.rs.shared.ScopeType;
import org.xdi.uma.demo.rs.shared.Scopes;
import org.xdi.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2013
 */
@Path("/scope")
public class ScopeWS {

    private static final Logger LOG = Logger.getLogger(ScopeWS.class);

    @GET
    @Produces({"application/json"})
    public Response getList(@Context HttpServletRequest httpRequest) {
        try {
            final String requestUrl = httpRequest.getRequestURL().toString(); // e.g. http://127.0.0.1:8888/ws/scope

            final List<String> scopeList = Lists.newArrayList();
            for (ScopeType s : ScopeType.values()) {
                scopeList.add(requestUrl + "/" + s.getValue());
            }

            final Scopes scopes = new Scopes();
            scopes.setScopeUrlList(scopeList);
            return Response.ok(Util.asJson(scopes)).build();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return PhoneWS.INTERNAL_ERROR_RESPONSE;
    }

    @GET
    @Path("{scopeid}")
    @Produces({"application/json"})
    public Response getList(@PathParam("scopeid") String id) {
        try {
            final ScopeType scopeType = ScopeType.fromValue(id);
            if (scopeType != null) {
                final ScopeDescription scopeDescription = new ScopeDescription();
                scopeDescription.setName(id);

                return Response.ok(Util.asJson(scopeDescription)).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return PhoneWS.INTERNAL_ERROR_RESPONSE;
    }
}

package org.xdi.uma.demo.rs.server;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.xdi.oxauth.client.uma.ResourceSetPermissionRegistrationService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.ResourceSetPermissionRequest;
import org.xdi.oxauth.model.uma.ResourceSetPermissionTicket;
import org.xdi.uma.demo.common.server.CommonUtils;
import org.xdi.uma.demo.common.server.Configuration;
import org.xdi.uma.demo.rs.shared.ScopeType;
import org.xdi.util.Pair;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 15/05/2013
 */

public class PermissionService {

    private static final Logger LOG = Logger.getLogger(PermissionService.class);

    private static final PermissionService INSTANCE = new PermissionService();

    private PermissionService() {
    }

    public static PermissionService getInstance() {
        return INSTANCE;
    }

    public boolean hasEnoughPermissions(List<ResourceSetPermissionRequest> p_list, String p_resourceId, List<ScopeType> p_scopes) {
        if (p_list != null && !p_list.isEmpty() && StringUtils.isNotBlank(p_resourceId)) {
            for (ResourceSetPermissionRequest p : p_list) {
                if (p.getResourceSetId().equals(p_resourceId)) {
                    final boolean hasScope = ScopeService.getInstance().hasAnyScope(p.getScopes(), p_scopes);
                    if (hasScope) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String registerPermission(String p_resourceId, List<ScopeType> p_scopes) {
        try {
            final ScopeService scopeService = ScopeService.getInstance();

            final ResourceSetPermissionRequest request = new ResourceSetPermissionRequest();
            request.setResourceSetId(p_resourceId);
            request.setScopes(scopeService.getScopesAsUrls(p_scopes));

            final ResourceSetPermissionRegistrationService permissionRegistrationService = UmaClientFactory.instance().createResourceSetPermissionRegistrationService(CommonUtils.getAmConfiguration());


            LOG.debug("Try to register permission on AM with request: " + CommonUtils.asJsonSilently(request));
            final Configuration c = Configuration.getInstance();
            final ResourceSetPermissionTicket t = permissionRegistrationService.registerResourceSetPermission(
                    "Bearer " + Utils.getPat().getAccessToken(), c.getUmaAmHost(), c.getRsHost(), request);
            if (t != null) {
                LOG.debug("Permission registered successfully, ticket: " + t.getTicket());
                return t.getTicket();
            }
        } catch (ClientResponseFailure e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.debug("Failed to register permission.");
        return "";
    }

    public Pair<Boolean, Response> hasEnoughPermissionsWithTicketRegistration(List<ResourceSetPermissionRequest> p_list, String p_resourceId, List<ScopeType> p_scopes) {
        final Pair<Boolean, Response> result = new Pair<Boolean, Response>(false, null);
        if (hasEnoughPermissions(p_list, p_resourceId, p_scopes)) {
            result.setFirst(true);
            return result;
        } else {
            // If the RPT is valid but has insufficient authorization data for the type of access sought,
            // the resource server SHOULD register a requested permission with the authorization server
            // that would suffice for that scope of access (see Section 3.2),
            // and then respond with the HTTP 403 (Forbidden) status code,
            // along with providing the authorization server's URI in an "as_uri" property in the header,
            // and the permission ticket it just received from the AM in the body in a JSON-encoded "ticket" property.
            result.setFirst(false);
            final String ticket = registerPermission(p_resourceId, p_scopes);
            //                    LOG.debug("Register permissions on AM, permission ticket: " + ticket);

            final String entity = CommonUtils.asJsonSilently(new ResourceSetPermissionTicket(ticket));

            LOG.debug("Construct response: HTTP 403 (Forbidden), entity: " + entity);
            final Response response = Response.status(Response.Status.FORBIDDEN).
                    header("host_id", Configuration.getInstance().getRsHost()).
                    header("as_uri", Configuration.getInstance().getUmaMetaDataUrl()).
                    header("error", "insufficient_scope").
                    entity(entity).
                    build();
            result.setSecond(response);
            return result;
        }
    }
}

package org.xdi.uma.demo.rs.server;

import org.apache.log4j.Logger;
import org.xdi.oxauth.model.uma.RptIntrospectionResponse;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.uma.demo.common.server.CommonUtils;
import org.xdi.uma.demo.common.server.Configuration;
import org.xdi.uma.demo.common.server.ref.IPat;
import org.xdi.uma.demo.rs.shared.ResourceType;
import org.xdi.uma.demo.rs.shared.ScopeType;
import org.xdi.util.InterfaceRegistry;
import org.xdi.util.StringHelper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2013
 */

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class);

    public static final Response INTERNAL_ERROR = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    private Utils() {
    }

    public static RptIntrospectionResponse extract(HttpServletRequest p_request) {
        return (RptIntrospectionResponse) p_request.getAttribute(RptPreProcessInterceptor.RPT_STATUS_ATTR_NAME);
    }

    public static Response unauthorizedResponse() {
        return Response.status(Response.Status.UNAUTHORIZED).
                header("host_id", Configuration.getInstance().getRsHost()).
                header("as_uri", Configuration.getInstance().getUmaMetaDataUrl()).
                build();
    }

    public static String getRptFromAuthorization(String authorizationHeader) {
        if (StringHelper.isNotEmpty(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length());
        }
        return null;
    }

    public static String createMsg(ScopeType p_type, ResourceType p_resourceType) {
        return String.format("Request - Action: %s, Resource: %s", p_type, p_resourceType);
    }

    public static Token getPat() {
        final Token pat = InterfaceRegistry.get(IPat.class);
        if (pat == null) {
            return obtainPat();
        }
        return pat;
    }

    public static Token obtainPat() {
        try {
            final Configuration c = Configuration.getInstance();
            LOG.trace("Try to obtain PAT token...");
            final Token patToken = CommonUtils.requestPat(c.getTokenUrl(), c.getUmaPatClientId(), c.getUmaPatClientSecret());
            if (patToken != null) {
                InterfaceRegistry.put(IPat.class, patToken);
                LOG.trace("PAT token is successfully obtained.");
                return patToken;
            }
            LOG.error("Failed to obtain PAT token.");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}

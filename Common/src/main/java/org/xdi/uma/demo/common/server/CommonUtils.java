package org.xdi.uma.demo.common.server;

import org.apache.log4j.Logger;
import org.xdi.oxauth.client.TokenClient;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.model.uma.UmaConfiguration;
import org.xdi.oxauth.model.uma.UmaScopeType;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.uma.demo.common.server.ref.ILogList;
import org.xdi.uma.demo.common.server.ref.IMetadataConfiguration;
import org.xdi.util.InterfaceRegistry;
import org.xdi.util.Util;

import java.io.IOException;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2013
 */

public class CommonUtils {

    private static final Logger LOG = Logger.getLogger(CommonUtils.class);

    private CommonUtils() {
    }

    public static LogList getLogList() {
        return InterfaceRegistry.get(ILogList.class);
    }

    public static UmaConfiguration getUmaConfiguration() {
        final UmaConfiguration result = InterfaceRegistry.get(IMetadataConfiguration.class);
        if (result == null) {
            final Configuration c = Configuration.getInstance();
            if (c != null) {
                final UmaConfiguration umaAmConfiguration = Uma.discovery(c.getUmaMetaDataUrl());
                if (umaAmConfiguration != null) {
                    InterfaceRegistry.put(IMetadataConfiguration.class, umaAmConfiguration);
                }
                return umaAmConfiguration;
            }
        }
        return result;
    }

    public static String asJsonSilently(Object p_object) {
        try {
            return Util.asPrettyJson(p_object);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }

    public static Token request(final String tokenUrl, final String umaClientId, final String umaClientSecret, UmaScopeType scopeType, String... scopeArray) throws Exception {

        String scope = scopeType.getValue();
        if (scopeArray != null && scopeArray.length > 0) {
            for (String s : scopeArray) {
                scope = scope + " " + s;
            }
        }

        TokenClient tokenClient = new TokenClient(tokenUrl);
        tokenClient.setExecutor(Uma.getClientExecutor());
        TokenResponse response = tokenClient.execClientCredentialsGrant(scope, umaClientId, umaClientSecret);

        if (response.getStatus() == 200) {
            final String accessToken = response.getAccessToken();
            final Integer expiresIn = response.getExpiresIn();
            if (org.xdi.oxauth.model.util.Util.allNotBlank(accessToken)) {
                return new Token(null, null, accessToken, scopeType.getValue(), expiresIn);
            }
        } else {
            LOG.error(response.getEntity());
        }

        return null;
    }

    public static Token requestPat(final String tokenUrl, final String umaClientId, final String umaClientSecret, String... scopeArray) throws Exception {
        return request(tokenUrl, umaClientId, umaClientSecret, UmaScopeType.PROTECTION, scopeArray);
    }

    public static Token requestAat(final String tokenUrl, final String umaClientId, final String umaClientSecret, String... scopeArray) throws Exception {
        return request(tokenUrl, umaClientId, umaClientSecret, UmaScopeType.AUTHORIZATION, scopeArray);
    }

}

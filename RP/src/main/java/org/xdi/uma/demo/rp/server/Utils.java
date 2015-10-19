package org.xdi.uma.demo.rp.server;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xdi.oxauth.client.uma.RequesterPermissionTokenService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.RequesterPermissionTokenResponse;
import org.xdi.uma.demo.common.server.CommonUtils;
import org.xdi.uma.demo.common.server.Configuration;
import org.xdi.uma.demo.common.server.ref.IRpt;
import org.xdi.util.InterfaceRegistry;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2013
 */

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class);

    private Utils() {
    }

//    public static Token getAat() {
//        final Token aat = InterfaceRegistry.get(IAatToken.class);
//        if (aat == null) {
//            return obtainAat();
//        }
//        return aat;
//    }
//
//    public static Token obtainAat() {
//        try {
//            final Configuration c = Configuration.getInstance();
//            LOG.debug("Try to obtain AAT token...");
//            final Token aatToken = UmaClient.requestAat(c.getAuthorizeUrl(), c.getTokenUrl(),
//                    c.getUmaUserId(), c.getUmaUserSecret(), c.getUmaAatClientId(), c.getUmaAatClientSecret(), c.getUmaRedirectUri());
//            if (aatToken != null) {
//                InterfaceRegistry.put(IAatToken.class, aatToken);
//                LOG.debug("AAT token is successfully obtained. Aat: " + aatToken.getAccessToken());
//                return aatToken;
//            }
//            LOG.error("Failed to obtain AAT token.");
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//        }
//        return null;
//    }

//    public static String getRpt() {
//        return getRpt(getAat());
//    }

    public static String getRpt(String p_aat) {
        final String rpt = InterfaceRegistry.get(IRpt.class);
        if (rpt == null) {
            return obtainRpt(p_aat);
        }
        return rpt;
    }

    public static String obtainRpt(String p_aat) {
        LOG.debug("Try to obtain RPT with AAT on Authorization Server... , aat:" + p_aat);
        try {
            final Configuration c = Configuration.getInstance();
            final RequesterPermissionTokenService rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(CommonUtils.getAmConfiguration());
            final RequesterPermissionTokenResponse rptResponse = rptService.getRequesterPermissionToken("Bearer " + p_aat, c.getUmaAmHost());
            if (rptResponse != null && StringUtils.isNotBlank(rptResponse.getToken())) {
                final String result = rptResponse.getToken();
                InterfaceRegistry.put(IRpt.class, result);
                LOG.debug("RPT is successfully obtained from AM. RPT: " + result);
                return result;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.debug("Failed to obtain RPT.");
        return null;
    }
}

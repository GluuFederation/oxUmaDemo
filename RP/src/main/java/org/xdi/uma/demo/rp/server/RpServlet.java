package org.xdi.uma.demo.rp.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.python.google.common.base.Strings;
import org.xdi.oxauth.client.uma.CreateRptService;
import org.xdi.oxauth.client.uma.RptAuthorizationRequestService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.*;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.uma.demo.rp.shared.Conf;
import org.xdi.uma.demo.common.gwt.Msg;
import org.xdi.uma.demo.common.gwt.Phones;
import org.xdi.uma.demo.common.server.CommonUtils;
import org.xdi.uma.demo.common.server.Configuration;
import org.xdi.uma.demo.common.server.Uma;
import org.xdi.uma.demo.common.server.ref.IAat;
import org.xdi.uma.demo.common.server.ref.IMetadataConfiguration;
import org.xdi.uma.demo.common.server.ref.IRpt;
import org.xdi.uma.demo.rp.client.LoginController;
import org.xdi.uma.demo.rp.client.Service;
import org.xdi.util.InterfaceRegistry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 20/05/2013
 */

public class RpServlet extends RemoteServiceServlet implements Service {

    private static final Logger LOG = Logger.getLogger(RpServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            final Configuration c = Configuration.getInstance();

            final UmaConfiguration umaAmConfiguration = Uma.discovery(c.getUmaMetaDataUrl());
            if (umaAmConfiguration != null) {
                InterfaceRegistry.put(IMetadataConfiguration.class, umaAmConfiguration);
                LOG.info("Loaded Authorization Server configuration: " + CommonUtils.asJsonSilently(umaAmConfiguration));
                LOG.info("RP Server started successfully.");
            } else {
                LOG.error("Unable to load Authorization Server configuration. Failed to start RP Server.");
                throw new ServletException();
            }

            Token aat = obtainAat();
            if (aat == null) {
                LOG.error("Failed to obtain AAT.");
                throw new ServletException();
            }

        } catch (Exception e) {
            LOG.error("Failed to start RP Demo Application. " + e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
    public List<Msg> getMessageList() {
        return CommonUtils.getLogList().getAll();
    }

    @Override
    public String obtainNewAat() {
        try {
            return obtainAat().getAccessToken();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String obtainNewRpt() {
        try {
            final String rpt = obtainRpt(getAat());
            if (StringUtils.isNotBlank(rpt)) {
                LOG.trace("RPT token obtained successfully.");
            }
            return rpt;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public String getAat() {
        final HttpServletRequest request = getThreadLocalRequest();
        final Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (c.getName().equals(LoginController.ACCESS_TOKEN_COOKIE_NAME)) {
                    LOG.trace("Returns AAT from cookie, aat: " + c.getValue());
                    return c.getValue();
                }
            }
        }
        LOG.trace("Returns empty AAT cookie.");
        return "";
//        String aat = InterfaceRegistry.get(IAatToken.class);
//        LOG.trace("Access aat: " + aat);
//        return aat;
    }

    @Override
    public void clearLogs() {
        LOG.trace("Cleared logs.");
        CommonUtils.getLogList().clear();
    }

    @Override
    public Phones demo() {
        try {
            final PhoneService phoneService = PhoneService.getInstance();
            try {
                LOG.debug("Call RS without RPT...");
                final Phones phones = phoneService.getPhonesVerbose("");
                if (phones != null) {
                    return phones;
                }
            } catch (Exception e) {
                LOG.debug("Response: unauthorized. RP doesn't present RPT.");
            }

            final Configuration c = Configuration.getInstance();

            final String aat = getAat();

            if (aat != null) {

                final String rpt = obtainRpt(aat);

                try {
                    final Phones phones = phoneService.getPhonesVerbose(rpt);
                    if (phones != null) {
                        return phones;
                    }
                } catch (ClientResponseFailure e) {
                    final ClientResponse<PermissionTicket> response = e.getResponse();
                    if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                        LOG.debug("Request forbidden. RPT doesn't have enough permissions.");
                        final PermissionTicket ticketWrapper = response.getEntity(PermissionTicket.class);
                        final String ticket = ticketWrapper.getTicket();
                        LOG.debug("RS returns permission ticket: " + ticket);
                        final RptAuthorizationRequest authorizationRequest = new RptAuthorizationRequest(rpt, ticket);


                        LOG.debug("Try to authorize RPT with ticket...");
                        final RptAuthorizationRequestService rptAuthorizationService = UmaClientFactory.instance().createAuthorizationRequestService(CommonUtils.getUmaConfiguration());
                        final RptAuthorizationResponse clientAuthorizationResponse = rptAuthorizationService.requestRptPermissionAuthorization(
                                "Bearer " + aat,
                                c.getUmaAmHost(),
                                authorizationRequest);
                        if (clientAuthorizationResponse != null && !Strings.isNullOrEmpty(clientAuthorizationResponse.getRpt())) {
                            LOG.debug("RPT is authorized.");

                            final Phones phones = phoneService.getPhonesVerbose(getRpt());
                            if (phones != null) {
                                return phones;
                            }
                        }
                    } else {
                        LOG.debug("Failed to run demo. Response status: " + response.getResponseStatus());
                    }
                }
            } else {
                LOG.debug("ERROR: AAT is null!!!");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Phones getPhoneList() {
        try {
            return PhoneService.getInstance().getPhones(getRpt(), getAat());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private String getRpt() {
        return getRpt(getAat());
    }


    @Override
    public boolean removePhone(String p_phone) {
        try {
            return PhoneService.getInstance().removePhone(getRpt(), getAat(), p_phone);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean addPhone(String p_phone) {
        try {
            return PhoneService.getInstance().addPhone(getRpt(), getAat(), p_phone);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void clearState() {
        clearLogs();
        //obtainNewAat();
        obtainNewRpt();
    }

    @Override
    public String getLoginUrl() {
        final String loginUrl = Configuration.getInstance().getLoginUrl();
        final String redirectUri = Configuration.getInstance().getRedirectUri();
        final String aatClientId = Configuration.getInstance().getUmaAatClientId();
        final String umaAuthorizationScope = "http%3A%2F%2Fdocs.kantarainitiative.org%2Fuma%2Fscopes%2Fauthz.json";
        final String result = String.format(loginUrl, umaAuthorizationScope, aatClientId, redirectUri);
        LOG.trace("getLoginUrl(), url: " + result);
        return result;
    }

    @Override
    public void storeAat(String p_accessToken) {
//        InterfaceRegistry.put(IAatToken.class, p_accessToken);

        final HttpServletRequest request = getThreadLocalRequest();
        final Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (c.getName().equals(LoginController.ACCESS_TOKEN_COOKIE_NAME)) {
                    LOG.trace("Set AAT cookie path to '/'");
                    c.setPath("/");
                }
            }
        }
    }

    @Override
    public Conf getConf() {
        final Configuration serverConf = Configuration.getInstance();

        Conf conf = new Conf();
        conf.setAmHost(serverConf.getUmaAmHost());
        conf.setRsHost(serverConf.getRsHost());
        return conf;
    }

    public String getRpt(String aat) {
        final String rpt = InterfaceRegistry.get(IRpt.class);
        if (rpt == null) {
            return obtainRpt(aat);
        }
        return rpt;
    }

    public static String obtainRpt(String aat) {
        LOG.debug("Try to obtain RPT with AAT on Authorization Server... , aat:" + aat);
        try {
            final Configuration c = Configuration.getInstance();
            final CreateRptService rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(CommonUtils.getUmaConfiguration(), Uma.getClientExecutor());
            final RPTResponse rptResponse = rptService.createRPT("Bearer " + aat, c.getUmaAmHost());
            if (rptResponse != null && StringUtils.isNotBlank(rptResponse.getRpt())) {
                final String result = rptResponse.getRpt();
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

    public Token obtainAat() {
        try {
            final Configuration c = Configuration.getInstance();
            LOG.trace("Try to obtain AAT token...");
            final Token aatToken = CommonUtils.requestAat(c.getTokenUrl(), c.getUmaAatClientId(), c.getUmaAatClientSecret());
            if (aatToken != null) {
                InterfaceRegistry.put(IAat.class, aatToken);
                LOG.trace("AAT token is successfully obtained.");
                return aatToken;
            }
            LOG.error("Failed to obtain AAT token.");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}

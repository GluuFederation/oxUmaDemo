package org.xdi.uma.demo.rp.server;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.python.google.common.base.Strings;
import org.xdi.oxauth.client.uma.RptAuthorizationRequestService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.PermissionTicket;
import org.xdi.oxauth.model.uma.RptAuthorizationRequest;
import org.xdi.oxauth.model.uma.RptAuthorizationResponse;
import org.xdi.uma.demo.common.gwt.Phones;
import org.xdi.uma.demo.common.gwt.RsResponse;
import org.xdi.uma.demo.common.gwt.Status;

import javax.ws.rs.core.Response;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 15/05/2013
 */

public class PhoneService {

    private static final Logger LOG = Logger.getLogger(PhoneService.class);

    private static final PhoneService INSTANCE = new PhoneService();

    private volatile PhoneClientService service = null;

    private PhoneService() {
        final Configuration c = Configuration.getInstance();
        service = ProxyFactory.create(PhoneClientService.class, c.getRsPhoneWsUrl());
    }

    public static PhoneService getInstance() {
        return INSTANCE;
    }

    public PhoneClientService service() {
        return service;
    }

    public Phones getPhonesVerbose(String p_rpt) {
        LOG.debug("Try to get (view) phones... , rpt:" + p_rpt);
        final Phones phones = service().getPhones("Bearer " + p_rpt, Configuration.getInstance().amHost());
        if (phones != null) {
            LOG.debug("Got phones from client: " + CommonUtils.asJsonSilently(phones));
            return phones;
        }
        LOG.error("Failed to get (view) phones.");
        return null;
    }

    public Phones getPhones(String p_rpt, String p_aat) {
        try {
            return getPhonesVerbose(p_rpt);
        } catch (ClientResponseFailure e) {
            final boolean authorized = authorizeRpt(p_rpt, p_aat, e);
            if (authorized) {
                LOG.trace("Re-try, rpt is authorized now...");
                final Phones phones = getPhonesVerbose(p_rpt);
                if (phones != null) {
                    return phones;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean authorizeRpt(String p_rpt, String p_aat, ClientResponseFailure p_failureResponse) {
        final ClientResponse<PermissionTicket> response = p_failureResponse.getResponse();
        if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            LOG.debug("Request forbidden. RPT doesn't have enough permissions.");
            final PermissionTicket ticketWrapper = response.getEntity(PermissionTicket.class);
            final String ticket = ticketWrapper.getTicket();
            LOG.debug("RS returns permission ticket: " + ticket);
            final RptAuthorizationRequest authorizationRequest = new RptAuthorizationRequest(p_rpt, ticket);

            final Configuration c = Configuration.getInstance();
            LOG.debug("Try to authorize RPT with ticket...");
            final RptAuthorizationRequestService rptAuthorizationService = UmaClientFactory.instance().createAuthorizationRequestService(CommonUtils.getUmaConfiguration());
            final RptAuthorizationResponse clientAuthorizationResponse = rptAuthorizationService.requestRptPermissionAuthorization(
                    "Bearer " + p_aat,
                    c.amHost(),
                    authorizationRequest);
            if (clientAuthorizationResponse != null && !Strings.isNullOrEmpty(clientAuthorizationResponse.getRpt())) {
                LOG.debug("RPT is authorized.");
                return true;
            }
        } else {
            LOG.debug("Authorization is possible only if status is FORBIDDEN. Response status: " + response.getResponseStatus() + " message: " + p_failureResponse.getMessage());
        }
        LOG.debug("Failed to authorize RPT.");
        return false;
    }

    private boolean addPhoneImpl(String p_rpt, String p_phone) {
        LOG.debug("Try to add phone number: " + p_phone);
        final RsResponse response = service().add("Bearer " + p_rpt, Configuration.getInstance().amHost(), p_phone);
        if (response != null) {
            LOG.debug("Phone added successfully. Phone added: " + p_phone);
            return response.getStatus() == Status.CREATED;
        }
        LOG.debug("Failed to add phone: " + p_phone);
        return false;
    }

    public boolean addPhone(String p_rpt, String p_aat, String p_phone) {
        try {
            return addPhoneImpl(p_rpt, p_phone);
        } catch (ClientResponseFailure e) {
            final boolean authorized = authorizeRpt(p_rpt, p_aat, e);
            if (authorized) {
                LOG.trace("Re-try, rpt is authorized now...");
                return addPhoneImpl(p_rpt, p_phone);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    private boolean removePhoneImpl(String p_rpt, String p_phone) {
        LOG.debug("Try to remove phone numbers: " + p_phone);
        final RsResponse response = service().remove("Bearer " + p_rpt, Configuration.getInstance().amHost(), p_phone);
        if (response != null) {
            LOG.debug("Phone removed successfully. Phone: " + p_phone);
            return response.getStatus() == Status.DELETED;
        }
        LOG.debug("Failed to remove phone: " + p_phone);
        return false;
    }

    public boolean removePhone(String p_rpt, String p_aat, String p_phone) {
        try {
            return removePhoneImpl(p_rpt, p_phone);
        } catch (ClientResponseFailure e) {
            final boolean authorized = authorizeRpt(p_rpt, p_aat, e);
            if (authorized) {
                LOG.trace("Re-try, rpt is authorized now...");
                return removePhoneImpl(p_rpt, p_phone);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return false;
    }
}

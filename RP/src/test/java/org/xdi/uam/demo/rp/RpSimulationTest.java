package org.xdi.uam.demo.rp;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.python.google.common.base.Strings;
import org.testng.annotations.Test;
import org.xdi.oxauth.client.uma.CreateRptService;
import org.xdi.oxauth.client.uma.RptAuthorizationRequestService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.uma.*;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.uma.demo.common.gwt.Phones;
import org.xdi.uma.demo.rp.server.Configuration;
import org.xdi.uma.demo.rp.server.PhoneService;
import org.xdi.uma.demo.rp.server.Uma;
import org.xdi.util.InterfaceRegistry;
import org.xdi.util.Util;

import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 15/05/2013
 */

public class RpSimulationTest {

    @Test
    public void testRpt() {
        final Configuration c = Configuration.getInstance();
        if (c != null) {
            final UmaConfiguration umaAmConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(c.getUmaMetaDataUrl()).getMetadataConfiguration();
            if (umaAmConfiguration != null) {
                InterfaceRegistry.put(UmaConfiguration.class, umaAmConfiguration);
            }
        }

        //RpServlet.obtainRpt("8a740bcb-b8e2-4301-a124-7d9b58582285");
    }

    @Test
    public void workflow() throws Exception {

        try {
            doCall("", "");
        } catch (Exception e) {
            System.out.println("Response: unauthorized.");
        }

        final UmaConfiguration umaConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(Configuration.getInstance().getUmaMetaDataUrl(), Uma.getClientExecutor()).getMetadataConfiguration();
        final CreateRptService rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(umaConfiguration);
        final Configuration c = Configuration.getInstance();

        final Token aat = UmaClient.requestAat(c.getTokenUrl(), c.getUmaAatClientId(), c.getUmaAatClientSecret());

        if (aat != null) {
            final RPTResponse rptResponse = rptService.createRPT("Bearer " + aat.getAccessToken(), c.amHost());

            try {
                doCall(rptResponse.getRpt(), aat.getAccessToken());
            } catch (ClientResponseFailure e) {
                final ClientResponse<PermissionTicket> response = e.getResponse();
                if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                    System.out.println("Request forbidden.");
                    final PermissionTicket ticketWrapper = response.getEntity(PermissionTicket.class);
                    final String ticket = ticketWrapper.getTicket();
                    System.out.println("RS returns permission ticket: " + ticket);
                    final RptAuthorizationRequest authorizationRequest = new RptAuthorizationRequest(rptResponse.getRpt(), ticket);


                    System.out.println("Try to authorize RPT with ticket...");
                    final RptAuthorizationRequestService rptAuthorizationService = UmaClientFactory.instance().createAuthorizationRequestService(umaConfiguration);
                    final RptAuthorizationResponse clientAuthorizationResponse = rptAuthorizationService.requestRptPermissionAuthorization(
                            "Bearer " + aat.getAccessToken(),
                            c.amHost(),
                            authorizationRequest);
                    if (clientAuthorizationResponse != null && !Strings.isNullOrEmpty(clientAuthorizationResponse.getRpt())) {
                        System.out.println("RPT is authorized.");

                        doCall(clientAuthorizationResponse.getRpt(), aat.getAccessToken());
                    }
                }
            }
        } else {
            System.out.println("ERROR: AAT is null!!!");
        }
    }

    private void doCall(String p_rpt, String p_aat) throws IOException {
        System.out.println("Call phones service...");
        final PhoneService phoneService = PhoneService.getInstance();
        final Phones phones = phoneService.getPhones(p_rpt, p_aat);
        System.out.println(Util.asPrettyJson(phones));
    }
}

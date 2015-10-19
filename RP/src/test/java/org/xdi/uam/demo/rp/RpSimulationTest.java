package org.xdi.uam.demo.rp;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.testng.annotations.Test;
import org.xdi.oxauth.client.uma.AuthorizationRequestService;
import org.xdi.oxauth.client.uma.RequesterPermissionTokenService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.uma.AuthorizationResponse;
import org.xdi.oxauth.model.uma.MetadataConfiguration;
import org.xdi.oxauth.model.uma.RequesterPermissionTokenResponse;
import org.xdi.oxauth.model.uma.ResourceSetPermissionTicket;
import org.xdi.oxauth.model.uma.RptAuthorizationRequest;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.uma.demo.common.gwt.Phones;
import org.xdi.uma.demo.common.server.Configuration;
import org.xdi.uma.demo.common.server.ref.IMetadataConfiguration;
import org.xdi.uma.demo.rp.server.PhoneService;
import org.xdi.uma.demo.rp.server.Utils;
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
            final MetadataConfiguration umaAmConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(c.getUmaMetaDataUrl()).getMetadataConfiguration();
            if (umaAmConfiguration != null) {
                InterfaceRegistry.put(IMetadataConfiguration.class, umaAmConfiguration);
            }
        }


        final String aat = "8a740bcb-b8e2-4301-a124-7d9b58582285";
        Utils.obtainRpt(aat);
    }

    @Test
    public void test() throws Exception {

        try {
            doCall("", "");
        } catch (Exception e) {
            System.out.println("Response: unauthorized.");
        }

        final String umaMetaDataUrl = "https://seed.gluu.org/oxauth/seam/resource/restv1/oxauth/uma-configuration";
        final MetadataConfiguration metadataConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(umaMetaDataUrl).getMetadataConfiguration();
        final RequesterPermissionTokenService rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(metadataConfiguration);
        final Configuration c = Configuration.getInstance();

        final Token aat = UmaClient.requestAat(c.getAuthorizeUrl(), c.getTokenUrl(), c.getUmaUserId(), c.getUmaUserSecret(),
                c.getUmaAatClientId(), c.getUmaAatClientSecret(), c.getUmaRedirectUri());

        if (aat != null) {
            final RequesterPermissionTokenResponse rptResponse = rptService.getRequesterPermissionToken("Bearer " + aat.getAccessToken(), c.getUmaAmHost());

            try {
                doCall(rptResponse.getToken(), aat.getAccessToken());
            } catch (ClientResponseFailure e) {
                final ClientResponse<ResourceSetPermissionTicket> response = e.getResponse();
                if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                    System.out.println("Request forbidden.");
                    final ResourceSetPermissionTicket ticketWrapper = response.getEntity(ResourceSetPermissionTicket.class);
                    final String ticket = ticketWrapper.getTicket();
                    System.out.println("RS returns permission ticket: " + ticket);
                    final RptAuthorizationRequest authorizationRequest = new RptAuthorizationRequest(rptResponse.getToken(), ticket);


                    System.out.println("Try to authorize RPT with ticket...");
                    final AuthorizationRequestService rptAuthorizationService = UmaClientFactory.instance().createAuthorizationRequestService(metadataConfiguration);
                    final ClientResponse<AuthorizationResponse> clientAuthorizationResponse = rptAuthorizationService.requestRptPermissionAuthorization(
                            "Bearer " + aat.getAccessToken(),
                            c.getUmaAmHost(),
                            authorizationRequest);
                    final AuthorizationResponse authorizationResponse = clientAuthorizationResponse.getEntity();
                    if (authorizationResponse != null) {
                        System.out.println("RPT is authorized.");

                        doCall(rptResponse.getToken(), aat.getAccessToken());
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

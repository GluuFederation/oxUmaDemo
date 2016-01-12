package org.xdi.uma.demo.rs.server;

import org.xdi.oxauth.client.ClientUtils;
import org.xdi.oxauth.client.RegisterClient;
import org.xdi.oxauth.client.RegisterRequest;
import org.xdi.oxauth.client.RegisterResponse;
import org.xdi.oxauth.model.common.ResponseType;
import org.xdi.oxauth.model.register.ApplicationType;
import org.xdi.oxauth.model.util.StringUtils;
import org.xdi.uma.demo.common.server.Uma;

import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/01/2016
 */

public class RegisterClientManual {

    public static void main(String[] args) throws Exception {

        String registrationEndpoint = "https://localhost:8443/seam/resource/restv1/oxauth/register";

        RegisterRequest registerRequest = new RegisterRequest(ApplicationType.WEB, "oxUma Demo",
                StringUtils.spaceSeparatedToList("https://client.example.com/cb"));
        registerRequest.setResponseTypes(Arrays.asList(ResponseType.CODE, ResponseType.ID_TOKEN, ResponseType.TOKEN));

        RegisterClient registerClient = new RegisterClient(registrationEndpoint);
        registerClient.setRequest(registerRequest);
        registerClient.setExecutor(Uma.getClientExecutor());
        RegisterResponse registerResponse = registerClient.exec();

        ClientUtils.showClient(registerClient);
        assertEquals(registerResponse.getStatus(), 200, "Unexpected response code: " + registerResponse.getEntity());
        assertNotNull(registerResponse.getClientId());
        assertNotNull(registerResponse.getClientSecret());
        assertNotNull(registerResponse.getRegistrationAccessToken());
        assertNotNull(registerResponse.getClientIdIssuedAt());
        assertNotNull(registerResponse.getClientSecretExpiresAt());
    }
}

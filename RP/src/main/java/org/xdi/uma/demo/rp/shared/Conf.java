package org.xdi.uma.demo.rp.shared;

import java.io.Serializable;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/01/2016
 */

public class Conf implements Serializable {

    private String amHost;
    private String rsHost;
    private AuthenticationType authenticationType = AuthenticationType.CLIENT_AUTHENTICATION;

    public String getAmHost() {
        return amHost;
    }

    public void setAmHost(String amHost) {
        this.amHost = amHost;
    }

    public String getRsHost() {
        return rsHost;
    }

    public void setRsHost(String rsHost) {
        this.rsHost = rsHost;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    @Override
    public String toString() {
        return "Conf{" +
                "amHost='" + amHost + '\'' +
                ", rsHost='" + rsHost + '\'' +
                ", authenticationType=" + authenticationType +
                '}';
    }
}

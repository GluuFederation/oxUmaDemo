package org.xdi.uma.demo.common.gwt;

import java.io.Serializable;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/01/2016
 */

public class Conf implements Serializable {

    private String amHost;
    private String rsHost;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Conf");
        sb.append("{amHost='").append(amHost).append('\'');
        sb.append(", rsHost='").append(rsHost).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

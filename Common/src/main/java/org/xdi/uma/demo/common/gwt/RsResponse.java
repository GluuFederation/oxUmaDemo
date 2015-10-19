package org.xdi.uma.demo.common.gwt;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2013
 */

public class RsResponse implements Serializable {

    @JsonProperty(value = "status")
    private Status status;

    public RsResponse() {
    }

    public RsResponse(Status p_status) {
        status = p_status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status p_status) {
        status = p_status;
    }
}

package org.xdi.uma.demo.rp.server;

import org.xdi.uma.demo.common.gwt.Phones;
import org.xdi.uma.demo.common.gwt.RsResponse;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 15/05/2013
 */

public interface PhoneClientService {
    @GET
    @Produces({"application/json"})
    public Phones getPhones(@HeaderParam("Authorization") String authorization, @HeaderParam("Host") String rsHost);

    @PUT
    @Path("{phone}")
    @Produces({"application/json"})
    public RsResponse add(@HeaderParam("Authorization") String authorization, @HeaderParam("Host") String rsHost, @PathParam("phone") String phone);

    @DELETE
    @Path("{phone}")
    @Produces({"application/json"})
    public RsResponse remove(@HeaderParam("Authorization") String authorization, @HeaderParam("Host") String rsHost, @PathParam("phone") String phone);
}

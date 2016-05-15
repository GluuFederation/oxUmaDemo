package org.xdi.uma.demo.rs.server;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.xdi.oxd.rs.protect.StaticStorage;
import org.xdi.oxd.rs.protect.resteasy.ResourceRegistrar;
import org.xdi.oxd.rs.protect.resteasy.RptPreProcessInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 05/03/2016
 */
@Provider
@ServerInterceptor
public class UmaProtector implements PreProcessInterceptor {

    private static final Logger LOG = Logger.getLogger(UmaProtector.class);

    private RptPreProcessInterceptor interceptor;

    public UmaProtector() {
        try {
            interceptor = new RptPreProcessInterceptor(StaticStorage.get(ResourceRegistrar.class));
            LOG.info("UMA Protector started successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HTTP interceptor.", e);
        }
    }

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
        return interceptor.preProcess(request, method);
    }
}

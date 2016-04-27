package org.xdi.uma.demo.rs.server;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.xdi.oxd.rs.protect.resteasy.ConfigurationLoader;
import org.xdi.oxd.rs.protect.resteasy.PatProvider;
import org.xdi.oxd.rs.protect.resteasy.ResourceRegistrar;
import org.xdi.oxd.rs.protect.resteasy.RptPreProcessInterceptor;
import org.xdi.oxd.rs.protect.resteasy.ServiceProvider;

import javax.ws.rs.ext.Provider;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 17/05/2013
 */
@Provider
@ServerInterceptor
public class PreProcessInterceptor extends RptPreProcessInterceptor {

    public PreProcessInterceptor() {
        super(new ResourceRegistrar(new PatProvider(new ServiceProvider(ConfigurationLoader.loadFromJson(ConfigurationLoader.class.getClassLoader().getResourceAsStream("rs-protect.json"))))));
    }
}

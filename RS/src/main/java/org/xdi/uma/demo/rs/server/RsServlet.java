package org.xdi.uma.demo.rs.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xdi.oxd.rs.protect.RsProtector;
import org.xdi.oxd.rs.protect.RsResource;
import org.xdi.oxd.rs.protect.StaticStorage;
import org.xdi.oxd.rs.protect.resteasy.*;
import org.xdi.uma.demo.common.gwt.Msg;
import org.xdi.uma.demo.common.server.ConfigurationLocator;
import org.xdi.uma.demo.common.server.LogList;
import org.xdi.uma.demo.common.server.ref.ILogList;
import org.xdi.uma.demo.rs.client.Service;
import org.xdi.util.InterfaceRegistry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2013
 */

public class RsServlet extends RemoteServiceServlet implements Service {

    private static final Logger LOG = Logger.getLogger(RsServlet.class);

    public static final String CONFIGURATION_FILE_NAME = "rs-protect-config.json";
    public static final String PROTECTION_CONFIGURATION_FILE_NAME = "rs-protect.json";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            Configuration configuration = ConfigurationLoader.loadFromJson(inputStream(CONFIGURATION_FILE_NAME));
            LOG.info("Loaded configuration: " + configuration);

            Collection<RsResource> values = RsProtector.instance(inputStream(PROTECTION_CONFIGURATION_FILE_NAME)).getResourceMap().values();

            LOG.info("Protection configuration: " + IOUtils.toString(inputStream(PROTECTION_CONFIGURATION_FILE_NAME)));

            ServiceProvider serviceProvider = new ServiceProvider(configuration);
            ObtainPatProvider patProvider = new ObtainPatProvider(serviceProvider);
            ResourceRegistrar resourceRegistrar = new ResourceRegistrar(patProvider, serviceProvider);

            resourceRegistrar.register(values);
            LOG.info("Resources are registered at AS: " + configuration.getUmaWellknownEndpoint());

            StaticStorage.put(PatProvider.class, patProvider);
            StaticStorage.put(ResourceRegistrar.class, resourceRegistrar);

            LOG.info("Resource Server started successfully.");
        } catch (Exception e) {
            LOG.error("Failed to start Resource Server. " +e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    private InputStream inputStream(String fileName) throws FileNotFoundException {
        ClassLoader classLoader = ConfigurationLoader.class.getClassLoader();
        File file = new File(ConfigurationLocator.getDir() + fileName);
        if (file.exists()) {
            LOG.trace("Configuration file location: " + ConfigurationLocator.getDir() + fileName);
            return new FileInputStream(file);
        }
        LOG.trace("Loading configuration from class path: " + fileName);
        return classLoader.getResourceAsStream(fileName);
    }

    @Override
    public List<Msg> getMessageList() {
        return InterfaceRegistry.<LogList>get(ILogList.class).getAll();
    }

    @Override
    public String obtainNewPat() {
        return StaticStorage.get(ObtainPatProvider.class).renewPat();
    }

    @Override
    public void clearLogs() {
        InterfaceRegistry.<LogList>get(ILogList.class).clear();
    }

}

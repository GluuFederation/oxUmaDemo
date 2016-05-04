package org.xdi.uma.demo.rs.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.xdi.oxd.rs.protect.RsProtector;
import org.xdi.oxd.rs.protect.RsResource;
import org.xdi.oxd.rs.protect.resteasy.*;
import org.xdi.uma.demo.common.gwt.Msg;
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

    static {
        if ((System.getProperty("catalina.base") != null) && (System.getProperty("catalina.base.ignore") == null)) {
            BASE_DIR = System.getProperty("catalina.base");
        } else if (System.getProperty("catalina.home") != null) {
            BASE_DIR = System.getProperty("catalina.home");
        } else if (System.getProperty("jboss.home.dir") != null) {
            BASE_DIR = System.getProperty("jboss.home.dir");
        } else {
            BASE_DIR = null;
        }
    }

    private static final String BASE_DIR;
    private static final String DIR = BASE_DIR + File.separator + "conf" + File.separator;

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
            PatProvider patProvider = new PatProvider(serviceProvider);
            ResourceRegistrar resourceRegistrar = new ResourceRegistrar(patProvider);

            resourceRegistrar.register(values);
            LOG.info("Resources are registered at AS: " + configuration.getUmaWellknownEndpoint());

            ResteasyProviderFactory.pushContext(PatProvider.class, patProvider);
            ResteasyProviderFactory.pushContext(ResourceRegistrar.class, resourceRegistrar);

            LOG.info("Resource Server started successfully.");
        } catch (Exception e) {
            LOG.error("Failed to start Resource Server. " +e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    private InputStream inputStream(String fileName) throws FileNotFoundException {
        ClassLoader classLoader = ConfigurationLoader.class.getClassLoader();
        File file = new File(DIR + fileName);
        if (file.exists()) {
            LOG.trace("Configuration file location: " + DIR + fileName);
            return new FileInputStream(file);
        }
        return classLoader.getResourceAsStream(fileName);
    }

    @Override
    public List<Msg> getMessageList() {
        return InterfaceRegistry.<LogList>get(ILogList.class).getAll();
    }

    @Override
    public String obtainNewPat() {
        return ResteasyProviderFactory.getContextData(PatProvider.class).renewPat();
    }

    @Override
    public void clearLogs() {
        InterfaceRegistry.<LogList>get(ILogList.class).clear();
    }

}

package org.xdi.uma.demo.rs.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.xdi.oxauth.model.uma.UmaConfiguration;
import org.xdi.oxd.rs.protect.RsProtector;
import org.xdi.oxd.rs.protect.RsResource;
import org.xdi.oxd.rs.protect.resteasy.ConfigurationLoader;
import org.xdi.oxd.rs.protect.resteasy.PatProvider;
import org.xdi.oxd.rs.protect.resteasy.ResourceRegistrar;
import org.xdi.oxd.rs.protect.resteasy.ServiceProvider;
import org.xdi.uma.demo.common.gwt.Msg;
import org.xdi.uma.demo.common.server.CommonUtils;
import org.xdi.uma.demo.common.server.Configuration;
import org.xdi.uma.demo.common.server.Uma;
import org.xdi.uma.demo.common.server.ref.IMetadataConfiguration;
import org.xdi.uma.demo.rs.client.Service;
import org.xdi.util.InterfaceRegistry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Collection;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2013
 */

public class RsServlet extends RemoteServiceServlet implements Service {

    private static final Logger LOG = Logger.getLogger(RsServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            final Configuration c = Configuration.getInstance();
            if (c != null) {
                final UmaConfiguration umaAmConfiguration = Uma.discovery(c.getUmaMetaDataUrl());
                if (umaAmConfiguration != null) {
                    InterfaceRegistry.put(IMetadataConfiguration.class, umaAmConfiguration);
                    LOG.info("Loaded UMA configuration: " + CommonUtils.asJsonSilently(umaAmConfiguration));

                    ClassLoader classLoader = ConfigurationLoader.class.getClassLoader();
                    org.xdi.oxd.rs.protect.resteasy.Configuration configuration = ConfigurationLoader.loadFromJson(classLoader.getResourceAsStream("rs-protect-config.json"));
                    Collection<RsResource> values = RsProtector.instance(classLoader.getResourceAsStream("rs-protect.json")).getResourceMap().values();

                    ServiceProvider serviceProvider = new ServiceProvider(configuration);
                    PatProvider patProvider = new PatProvider(serviceProvider);
                    ResourceRegistrar resourceRegistrar = new ResourceRegistrar(patProvider);

                    resourceRegistrar.register(values);

                    ResteasyProviderFactory.pushContext(PatProvider.class, patProvider);
                    ResteasyProviderFactory.pushContext(ResourceRegistrar.class, resourceRegistrar);

                    LOG.info("Resource Server started successfully.");
                } else {
                    LOG.error("Unable to load Authorization Server configuration. Failed to start Resource Server.");
                }

            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Msg> getMessageList() {
        return CommonUtils.getLogList().getAll();
    }

    @Override
    public String obtainNewPat() {
        PatProvider patProvider = ResteasyProviderFactory.getContextData(PatProvider.class);
        patProvider.clearPat();
        return patProvider.getPatToken();
    }

    @Override
    public void clearLogs() {
        CommonUtils.getLogList().clear();
    }

}

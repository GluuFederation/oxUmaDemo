package org.xdi.uma.demo.common.server;

import org.apache.log4j.Logger;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.MetadataConfiguration;
import org.xdi.uma.demo.common.server.ref.ILogList;
import org.xdi.uma.demo.common.server.ref.IMetadataConfiguration;
import org.xdi.util.InterfaceRegistry;
import org.xdi.util.Util;

import java.io.IOException;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2013
 */

public class CommonUtils {

    private static final Logger LOG = Logger.getLogger(CommonUtils.class);

    private CommonUtils() {
    }

    public static LogList getLogList() {
        return InterfaceRegistry.get(ILogList.class);
    }

    public static MetadataConfiguration getAmConfiguration() {
        final MetadataConfiguration result = InterfaceRegistry.get(IMetadataConfiguration.class);
        if (result == null) {
            final Configuration c = Configuration.getInstance();
            if (c != null) {
                final MetadataConfiguration umaAmConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(c.getUmaMetaDataUrl()).getMetadataConfiguration();
                if (umaAmConfiguration != null) {
                    InterfaceRegistry.put(IMetadataConfiguration.class, umaAmConfiguration);
                }
                return umaAmConfiguration;
            }
        }
        return result;
    }

    public static String asJsonSilently(Object p_object) {
        try {
            return Util.asPrettyJson(p_object);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }
}

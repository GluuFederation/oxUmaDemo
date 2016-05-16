package org.xdi.uma.demo.rp.server;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;
import org.python.google.common.base.Strings;
import org.xdi.uma.demo.common.server.ConfigurationLocator;
import org.xdi.util.Util;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 14/05/2013
 */

public class Configuration {

    private static String APP_SERVER = System.getProperty("app.server");

    private static final Logger LOG = Logger.getLogger(Configuration.class);

    static {
        if (!Strings.isNullOrEmpty(APP_SERVER)) {
            APP_SERVER = "-" + APP_SERVER;
        } else {
            APP_SERVER = "";
        }
    }

    public static final String FILE_NAME = "oxuma-rp-conf" + APP_SERVER + ".json";

    private static class Holder {
        private static final Configuration CONF = createConfiguration();

        private static Configuration createConfiguration() {
            try {
                Configuration configuration = null;
                final File file = new File(ConfigurationLocator.getDir() + FILE_NAME);
                if (file.exists()) {
                    configuration = Util.createJsonMapper().readValue(file, Configuration.class);
                } else {
                    LOG.error("Unable to load RP configuration file from :" + file.getAbsolutePath());
                }

                if (configuration == null) {
                    final InputStream stream = Configuration.class.getClassLoader().getResourceAsStream(FILE_NAME);
                    configuration = Util.createJsonMapper().readValue(stream, Configuration.class);
                }

                if (configuration != null) {
                    LOG.info("RS configuration loaded successfully.");
                    return configuration;
                } else {
                    LOG.error("Failed to load RS configuration.");
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
            return null;
        }
    }

    public static Configuration getInstance() {
        return Holder.CONF;
    }

    @JsonProperty(value = "authorizeUrl")
    private String authorizeUrl;
    @JsonProperty(value = "tokenUrl")
    private String tokenUrl;
    @JsonProperty(value = "umaMetaDataUrl")
    private String umaMetaDataUrl;
    @JsonProperty(value = "umaAatClientId")
    private String umaAatClientId;
    @JsonProperty(value = "umaAatClientSecret")
    private String umaAatClientSecret;
    @JsonProperty(value = "rsPhoneWsUrl")
    private String rsPhoneWsUrl;
    @JsonProperty(value = "loginUrl")
    private String loginUrl;
    @JsonProperty(value = "redirectUri")
    private String redirectUri;

    public Configuration() {
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String p_redirectUri) {
        redirectUri = p_redirectUri;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String p_loginUrl) {
        loginUrl = p_loginUrl;
    }

    public String getUmaAatClientId() {
        return umaAatClientId;
    }

    public void setUmaAatClientId(String p_umaAatClientId) {
        umaAatClientId = p_umaAatClientId;
    }

    public String getUmaAatClientSecret() {
        return umaAatClientSecret;
    }

    public void setUmaAatClientSecret(String p_umaAatClientSecret) {
        umaAatClientSecret = p_umaAatClientSecret;
    }

    public String rsHost() {
        try {
            URI uri = new URI(redirectUri);
            return uri.getHost();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return "";
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(String p_authorizeUrl) {
        authorizeUrl = p_authorizeUrl;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String p_tokenUrl) {
        tokenUrl = p_tokenUrl;
    }

    public String getRsPhoneWsUrl() {
        return rsPhoneWsUrl;
    }

    public void setRsPhoneWsUrl(String p_rsPhoneWsUrl) {
        rsPhoneWsUrl = p_rsPhoneWsUrl;
    }

    public String amHost() {
        try {
            URI uri = new URI(umaMetaDataUrl);
            return uri.getHost();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return "";
    }

    public String getUmaMetaDataUrl() {
        return umaMetaDataUrl;
    }

    public void setUmaMetaDataUrl(String p_umaMetaDataUrl) {
        umaMetaDataUrl = p_umaMetaDataUrl;
    }
}

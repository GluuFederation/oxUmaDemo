package org.xdi.uma.demo.common.server;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;
import org.xdi.util.Util;

import java.io.InputStream;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 14/05/2013
 */

public class Configuration {

    private static final String APP_SERVER = System.getProperty("app.server");

    private static final Logger LOG = Logger.getLogger(Configuration.class);

//    private static final String BASE_DIR = System.getProperty("catalina.home") != null ?
//            System.getProperty("catalina.home") :
//            System.getProperty("jboss.home.dir");
//    private static final String DIR = BASE_DIR + File.separator + "conf" + File.separator;

    public static final String FILE_NAME = "oxuma-conf" + APP_SERVER + ".json";
//    public static final String FILE_PATH = DIR + FILE_NAME;

    private static class Holder {
        private static final Configuration CONF = createConfiguration();

        private static Configuration createConfiguration() {
            try {
                try {
//                    return Util.createJsonMapper().readValue(new File(FILE_PATH), Configuration.class);
                    final InputStream stream = Configuration.class.getResourceAsStream("/" + FILE_NAME);
                    final Configuration c = Util.createJsonMapper().readValue(stream, Configuration.class);
                    if (c != null) {
                        LOG.info("RS configuration loaded successfully.");
                    } else {
                        LOG.error("Failed to load RS configuration.");
                    }
                    return c;
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
                return null;
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
        }
    }

    public static Configuration getInstance() {
        return Holder.CONF;
    }

    @JsonProperty(value = "authorizeUrl")
    private String authorizeUrl;
    @JsonProperty(value = "tokenUrl")
    private String tokenUrl;
    @JsonProperty(value = "umaUserId")
    private String umaUserId;
    @JsonProperty(value = "umaUserSecret")
    private String umaUserSecret;
    @JsonProperty(value = "umaPatClientId")
    private String umaPatClientId;
    @JsonProperty(value = "umaPatClientSecret")
    private String umaPatClientSecret;
    @JsonProperty(value = "umaRedirectUri")
    private String umaRedirectUri;
    @JsonProperty(value = "umaAmHost")
    private String umaAmHost;
    @JsonProperty(value = "umaMetaDataUrl")
    private String umaMetaDataUrl;
    @JsonProperty(value = "rsScope")
    private String rsScope;
    @JsonProperty(value = "rsHost")
    private String rsHost;
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

    public String getRsHost() {
        return rsHost;
    }

    public void setRsHost(String p_rsHost) {
        rsHost = p_rsHost;
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

    public String getUmaUserId() {
        return umaUserId;
    }

    public void setUmaUserId(String p_umaUserId) {
        umaUserId = p_umaUserId;
    }

    public String getUmaUserSecret() {
        return umaUserSecret;
    }

    public void setUmaUserSecret(String p_umaUserSecret) {
        umaUserSecret = p_umaUserSecret;
    }

    public String getUmaPatClientId() {
        return umaPatClientId;
    }

    public void setUmaPatClientId(String p_umaPatClientId) {
        umaPatClientId = p_umaPatClientId;
    }

    public String getUmaPatClientSecret() {
        return umaPatClientSecret;
    }

    public String getRsPhoneWsUrl() {
        return rsPhoneWsUrl;
    }

    public void setRsPhoneWsUrl(String p_rsPhoneWsUrl) {
        rsPhoneWsUrl = p_rsPhoneWsUrl;
    }

    public void setUmaPatClientSecret(String p_umaPatClientSecret) {
        umaPatClientSecret = p_umaPatClientSecret;
    }

    public String getUmaRedirectUri() {
        return umaRedirectUri;
    }

    public void setUmaRedirectUri(String p_umaRedirectUri) {
        umaRedirectUri = p_umaRedirectUri;
    }

    public String getUmaAmHost() {
        return umaAmHost;
    }

    public void setUmaAmHost(String p_umaAmHost) {
        umaAmHost = p_umaAmHost;
    }

    public String getUmaMetaDataUrl() {
        return umaMetaDataUrl;
    }

    public void setUmaMetaDataUrl(String p_umaMetaDataUrl) {
        umaMetaDataUrl = p_umaMetaDataUrl;
    }

    public String getRsScope() {
        return rsScope;
    }

    public void setRsScope(String p_rsScope) {
        rsScope = p_rsScope;
    }
}

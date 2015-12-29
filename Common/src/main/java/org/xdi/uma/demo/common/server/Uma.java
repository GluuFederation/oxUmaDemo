package org.xdi.uma.demo.common.server;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.UmaConfiguration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 29/12/2015
 */

public class Uma {

    private Uma() {
    }

    public static UmaConfiguration discovery(String discoveryUrl) {
        try {
            return UmaClientFactory.instance().createMetaDataConfigurationService(discoveryUrl, getClientExecutor()).getMetadataConfiguration();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public static ClientExecutor getClientExecutor() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        return new ApacheHttpClient4Executor(createHttpClientTrustAll());
    }

    public static HttpClient createHttpClientTrustAll() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }, new AllowAllHostnameVerifier());

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, sf));
        ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);
        return new DefaultHttpClient(ccm);
    }
}

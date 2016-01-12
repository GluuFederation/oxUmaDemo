package org.xdi.uma.demo.rs.server;

import com.google.common.base.Preconditions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xdi.oxauth.client.uma.ResourceSetRegistrationService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.ResourceSet;
import org.xdi.oxauth.model.uma.ResourceSetResponse;
import org.xdi.oxauth.model.uma.UmaConfiguration;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.uma.demo.common.gwt.Msg;
import org.xdi.uma.demo.common.server.CommonUtils;
import org.xdi.uma.demo.common.server.Configuration;
import org.xdi.uma.demo.common.server.Uma;
import org.xdi.uma.demo.common.server.ref.IMetadataConfiguration;
import org.xdi.uma.demo.rs.client.Service;
import org.xdi.uma.demo.rs.shared.Resource;
import org.xdi.uma.demo.rs.shared.ResourceType;
import org.xdi.uma.demo.rs.shared.ScopeType;
import org.xdi.util.InterfaceRegistry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Arrays;
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

                    final Resource resource = registerResource();
                    ResourceRegistry.getInstance().put(ResourceType.PHONE, resource);

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
    public Resource registerResource() {

        try {
            final Token pat = Utils.getPat();

            if (pat != null) {
                final List<String> scopesAsUrls = ScopeService.getInstance().getScopesAsUrls(Arrays.asList(ScopeType.values()));

                final ResourceSet resourceSet = new ResourceSet();
                resourceSet.setName("oxUma Demo phones");
                resourceSet.setScopes(scopesAsUrls);

                LOG.debug("Register resource: " + CommonUtils.asJsonSilently(resourceSet));

                final ResourceSetRegistrationService registrationService = UmaClientFactory.instance().createResourceSetRegistrationService(CommonUtils.getUmaConfiguration(), Uma.getClientExecutor());
                final ResourceSetResponse response = registrationService.addResourceSet("Bearer " + pat.getAccessToken(), resourceSet);

                Preconditions.checkNotNull(response);
                Preconditions.checkState(StringUtils.isNotBlank(response.getId()));

                final Resource result = new Resource();
                result.setId(response.getId());
                LOG.debug("Resource registered, resource id: " + response.getId());
                return result;
            } else {
                LOG.debug("Unable to obtain PAT token. RS failed to protect resources by UMA.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String obtainNewPat() {
        try {
            final Token token = Utils.obtainPat();
            if (token != null) {
                return token.getAccessToken();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void clearLogs() {
        CommonUtils.getLogList().clear();
    }

}

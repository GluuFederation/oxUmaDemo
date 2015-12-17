package org.xdi.uma.demo.rs.server;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xdi.oxauth.client.uma.ResourceSetRegistrationService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.ResourceSet;
import org.xdi.oxauth.model.uma.ResourceSetStatus;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.uma.demo.common.server.CommonUtils;
import org.xdi.uma.demo.rs.shared.Resource;
import org.xdi.uma.demo.rs.shared.ResourceType;
import org.xdi.uma.demo.rs.shared.ScopeType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 15/05/2013
 */

public class ResourceRegistry {

    private static final Logger LOG = Logger.getLogger(ResourceRegistry.class);

    private static final ResourceRegistry INSTANCE = new ResourceRegistry();

    private final Map<ResourceType, Resource> m_map = new HashMap<ResourceType, Resource>();

    private ResourceRegistry() {
    }

    public static ResourceRegistry getInstance() {
        return INSTANCE;
    }

    public Resource getResourceId(ResourceType p_resourceType) {
        Resource resource = m_map.get(p_resourceType);
        if (resource == null) {
            resource = registerResource();
            put(ResourceType.PHONE, resource);
        }
        return resource;
    }

    public void put(ResourceType p_resourceType, Resource p_resource) {
        m_map.put(p_resourceType, p_resource);
    }

    public Resource registerResource() {
        final Token pat = Utils.getPat();
        if (pat != null) {
            final ResourceSet resourceSet = new ResourceSet();
            resourceSet.setName("Gluu phones");
            resourceSet.setScopes(ScopeService.getInstance().getScopesAsUrls(Arrays.asList(ScopeType.values())));

            final ResourceSetRegistrationService registrationService = UmaClientFactory.instance().createResourceSetRegistrationService(CommonUtils.getUmaConfiguration());
            final ResourceSetStatus status = registrationService.addResourceSet("Bearer " + pat.getAccessToken(), resourceSet);
            if (status != null && StringUtils.isNotBlank(status.getId())) {
                final Resource result = new Resource();
                result.setId(status.getId());
                LOG.debug("Resource registered, resource id: " + status.getId());
                return result;
            }
        } else {
            LOG.debug("PAT token is null, unable to register resource set.");
        }
        return null;
    }
}

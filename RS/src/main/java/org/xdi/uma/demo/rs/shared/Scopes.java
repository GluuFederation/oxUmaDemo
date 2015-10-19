package org.xdi.uma.demo.rs.shared;

import org.codehaus.jackson.annotate.JsonProperty;
import org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes;

import java.util.List;

/**
* @author Yuriy Zabrovarnyy
* @version 0.9, 08/05/2013
*/
@IgnoreMediaTypes("application/*+json") // try to ignore jettison as it's recommended here: http://docs.jboss.org/resteasy/docs/2.3.4.Final/userguide/html/json.html
public class Scopes {
    @JsonProperty(value = "scopes")
    private List<String> scopeUrlList;

    public List<String> getScopeUrlList() {
        return scopeUrlList;
    }

    public void setScopeUrlList(List<String> p_scopeUrlList) {
        scopeUrlList = p_scopeUrlList;
    }
}

package org.xdi.uma.demo.common.gwt;

import org.codehaus.jackson.annotate.JsonProperty;
import org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 13/05/2013
 */
@IgnoreMediaTypes("application/*+json")
// try to ignore jettison as it's recommended here: http://docs.jboss.org/resteasy/docs/2.3.4.Final/userguide/html/json.html
public class Phones implements Serializable {
    @JsonProperty(value = "phones")
    private List<String> phones;

    public Phones() {
    }

    public Phones(List<String> p_phones) {
        phones = p_phones;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> p_phones) {
        phones = p_phones;
    }
}

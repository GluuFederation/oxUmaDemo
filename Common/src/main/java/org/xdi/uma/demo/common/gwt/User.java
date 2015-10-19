package org.xdi.uma.demo.common.gwt;

import java.io.Serializable;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2013
 */

public class User implements Serializable {

    private String m_username;

    public String getUsername() {
        return m_username;
    }

    public void setUsername(String p_username) {
        m_username = p_username;
    }
}

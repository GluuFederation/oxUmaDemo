package org.xdi.uma.demo.rs.shared;

import java.io.Serializable;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 14/05/2013
 */

public class Resource implements Serializable {

    private String m_id;

    public Resource() {
    }

    public String getId() {
        return m_id;
    }

    public void setId(String p_id) {
        m_id = p_id;
    }
}

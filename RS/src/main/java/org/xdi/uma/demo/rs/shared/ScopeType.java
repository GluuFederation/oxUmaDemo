package org.xdi.uma.demo.rs.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2013
 */

public enum ScopeType {
    VIEW("view"),
    EDIT("edit"),
    ADD("add"),
    REMOVE("remove"),
    ALL("all");

    private static Map<String, ScopeType> lookup = new HashMap<String, ScopeType>();

    static {
        for (ScopeType enumType : values()) {
            lookup.put(enumType.getValue(), enumType);
        }
    }

    private final String m_value;

    private ScopeType(String p_value) {
        m_value = p_value;
    }

    public String getValue() {
        return m_value;
    }

    public static ScopeType fromValue(String p_value) {
        return lookup.get(p_value);
    }
}

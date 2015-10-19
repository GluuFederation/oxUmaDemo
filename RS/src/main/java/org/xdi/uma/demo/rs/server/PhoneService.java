package org.xdi.uma.demo.rs.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2013
 */

public class PhoneService {

    private static final PhoneService INSTANCE = new PhoneService();

    private final Set<String> m_set = new CopyOnWriteArraySet<String>(Arrays.asList(
            "555-55-551",
            "555-55-552",
            "555-55-553"
    ));

    private PhoneService() {
    }

    public static PhoneService getInstance() {
        return INSTANCE;
    }

    public boolean add(String p_phone) {
        return m_set.add(p_phone);
    }

    public boolean remove(String p_phone) {
        return m_set.remove(p_phone);
    }

    public Set<String> getPhones() {
        return new HashSet<String>(m_set);
    }

    public List<String> getPhoneList() {
        return new ArrayList<String>(m_set);
    }
}

package org.xdi.uma.demo.rs.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2013
 */

public class PhoneService {

    private static final PhoneService INSTANCE = new PhoneService();

    private final Set<String> m_set = Sets.newCopyOnWriteArraySet(Arrays.asList(
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

    public List<String> getPhoneList() {
        return Lists.newArrayList(m_set);
    }
}

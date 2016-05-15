package org.xdi.uma.demo.rp.server;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author yuriyz on 05/15/2016.
 */
public class StaticStorage {

    private static Map<Class<?>, Object> dataMap = Maps.newConcurrentMap();

    public StaticStorage() {
    }

    public static <T> T get(Class<T> type) {
        return (T) dataMap.get(type);
    }

    public static <T> void put(Class<T> type, T data) {
        dataMap.put(type, data);
    }
}

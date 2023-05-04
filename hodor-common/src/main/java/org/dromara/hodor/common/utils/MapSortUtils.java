package org.dromara.hodor.common.utils;

import java.util.*;

/**
 * MapSortUtils
 *
 * @author tomgs
 * @since 1.0
 */
public final class MapSortUtils {

    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<>(new KeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    public static Set<String> sortSetByKey(Set<String> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        TreeSet<String> result = new TreeSet<>(new KeyComparator());
        result.addAll(set);
        return result;
    }

    public static Set<String> sortByKey(Enumeration<String> enumeration) {
        if (enumeration == null) {
            return null;
        }
        TreeSet<String> result = new TreeSet<>(new KeyComparator());
        while (enumeration.hasMoreElements()) {
            result.add(enumeration.nextElement());
        }
        return result;
    }

    static class KeyComparator implements Comparator<String> {
        @Override
        public int compare(String key1, String key2) {
            return key1.compareTo(key2);
        }
    }
}
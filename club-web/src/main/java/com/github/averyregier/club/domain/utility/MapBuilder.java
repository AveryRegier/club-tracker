package com.github.averyregier.club.domain.utility;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by avery on 11/22/14.
 */
public class MapBuilder<K, V> {

    private Map<K, V> internal = new LinkedHashMap<>();

    public MapBuilder<K,V> put(K key, V value) {
        internal.put(key, value);
        return this;
    }

    public Map<K,V> build() {
        return internal;
    }
}

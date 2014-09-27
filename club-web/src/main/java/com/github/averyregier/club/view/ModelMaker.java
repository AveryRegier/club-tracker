package com.github.averyregier.club.view;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by avery on 9/27/14.
 */
public class ModelMaker {
    public <K,V> Map<K,V> toMap(K key, V value) {
        HashMap<K, V> kvHashMap = new HashMap<>();
        kvHashMap.put(key, value);
        return kvHashMap;
    }
}

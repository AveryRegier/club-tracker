package com.github.averyregier.club.view;

import com.github.averyregier.club.domain.User;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by avery on 9/27/14.
 */
public class ModelMaker {
    public <K,V> Map<K,V> toMap(K key, V value) {
        HashMap<K, V> kvHashMap = new HashMap<>();
        kvHashMap.put(key, value);
        return kvHashMap;
    }

    @SuppressWarnings("unchecked")
    protected User getUser(Request request) {
        return ((Optional<User>) request.attribute("user")).get();
    }
}

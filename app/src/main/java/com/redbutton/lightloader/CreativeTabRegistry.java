package com.redbutton.lightloader;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CreativeTabRegistry {
    private final Map<String, CreativeTab> tabs = new ConcurrentHashMap<>();

    public CreativeTab register(String id, String title, String iconResource, Class<?> resourceOwner) {
        CreativeTab tab = new CreativeTab(id, title, iconResource, resourceOwner.getClassLoader());
        if (tabs.putIfAbsent(id, tab) != null) {
            throw new IllegalArgumentException("Creative tab already registered: " + id);
        }
        return tab;
    }

    public Collection<CreativeTab> all() {
        return tabs.values();
    }
}
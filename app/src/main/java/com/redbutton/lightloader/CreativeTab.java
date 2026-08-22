package com.redbutton.lightloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class CreativeTab {
    private final String id;
    private final String title;
    private final String iconResource;
    private final ClassLoader resourceLoader;

    CreativeTab(String id, String title, String iconResource, ClassLoader resourceLoader) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.iconResource = Objects.requireNonNull(iconResource, "iconResource");
        this.resourceLoader = Objects.requireNonNull(resourceLoader, "resourceLoader");
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public InputStream openIcon() throws IOException {
        InputStream stream = resourceLoader.getResourceAsStream(iconResource);
        if (stream == null) {
            throw new IOException("Creative tab icon not found: " + iconResource);
        }
        return stream;
    }
}
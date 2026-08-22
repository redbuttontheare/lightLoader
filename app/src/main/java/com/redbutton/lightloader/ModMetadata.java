package com.redbutton.lightloader;

import java.util.Objects;
import java.util.Properties;

public record ModMetadata(String id, String name, String version, String minecraftVersion, String entrypoint) {
    public static ModMetadata from(Properties properties) {
        return new ModMetadata(
                required(properties, "id"),
                required(properties, "name"),
                required(properties, "version"),
                required(properties, "minecraft"),
                required(properties, "entrypoint"));
    }

    private static String required(Properties properties, String key) {
        return Objects.requireNonNull(properties.getProperty(key), "Missing metadata property: " + key);
    }
}
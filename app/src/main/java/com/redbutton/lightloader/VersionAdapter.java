package com.redbutton.lightloader;

public interface VersionAdapter {
    String minecraftVersion();

    void initialize(LoaderContext context);

    default void install(LoaderContext context) {
    }

    default void onWorldJoin(LoaderContext context, String worldName) {
        context.worldEvents().fireJoin(worldName);
    }
}
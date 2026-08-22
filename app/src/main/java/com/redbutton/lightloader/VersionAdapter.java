package com.redbutton.lightloader;

public interface VersionAdapter {
    String minecraftVersion();

    void initialize(LoaderContext context);
}
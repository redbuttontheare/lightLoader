package com.redbutton.lightloader.v1_26_2;

import com.redbutton.lightloader.LoaderContext;
import com.redbutton.lightloader.VersionAdapter;

public final class ChaosCubedAdapter implements VersionAdapter {
    @Override
    public String minecraftVersion() {
        return "26.2";
    }

    @Override
    public void initialize(LoaderContext context) {
        context.logger("minecraft-26.2").info("Chaos Cubed 26.2 adapter loaded");
    }
}
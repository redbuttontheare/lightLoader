package com.redbutton.lightloader.v1_26_2;

import com.redbutton.lightloader.LoaderContext;
import com.redbutton.lightloader.LightLoader;
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

    @Override
    public void install(LoaderContext context) {
        context.logger("minecraft-26.2").info("Ready for Chaos Cubed 26.2 class integration");
    }

    @Override
    public void onWorldJoin(LoaderContext context, String worldName) {
        LightLoader.onWorldJoin();
    }
}
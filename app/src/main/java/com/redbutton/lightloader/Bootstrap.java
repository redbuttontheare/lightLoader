package com.redbutton.lightloader;

public final class Bootstrap {
    private Bootstrap() {
    }

    public static void main(String[] args) throws Exception {
        LightLoader.launch(args);
        Class<?> minecraftMain = Class.forName("net.minecraft.client.main.Main");
        minecraftMain.getMethod("main", String[].class).invoke(null, (Object) args);
    }
}
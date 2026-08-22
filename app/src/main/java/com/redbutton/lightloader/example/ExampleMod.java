package com.redbutton.lightloader.example;

import com.redbutton.lightloader.LoaderContext;
import com.redbutton.lightloader.ModInitializer;

public final class ExampleMod implements ModInitializer {
    @Override
    public void onInitialize(LoaderContext context) {
        context.logger("example").info("Example mod loaded");
        context.chat().send("Example mod connected");
        context.commands().register(new ExampleCommand());
        context.storage().modDirectory("example");
        context.storage().worldDirectory("example");
        context.creativeTabs().register(
                "example",
                "Example",
                "assets/example/icon.svg",
                ExampleMod.class);
    }

    private static final class ExampleCommand implements com.redbutton.lightloader.Command {
        @Override
        public String name() {
            return "example";
        }

        @Override
        public void execute(com.redbutton.lightloader.CommandContext context) {
            context.chat().send("Example command executed");
        }
    }
}
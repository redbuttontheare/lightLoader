package com.redbutton.lightloader;

import java.lang.instrument.Instrumentation;
import java.util.Objects;
import java.util.logging.Logger;
import java.nio.file.Path;

public final class LoaderContext {
    private final Instrumentation instrumentation;
    private final Chat chat;
    private final CommandManager commands;
    private final ModStorage storage;
    private final CreativeTabRegistry creativeTabs;
    private final WorldEvents worldEvents;

    LoaderContext(Instrumentation instrumentation, Path modsDirectory, Path worldDirectory) {
        this.instrumentation = instrumentation;
        this.chat = message -> Logger.getLogger("LightLoader/Chat").info(message);
        this.commands = new CommandManager();
        this.storage = new ModStorage(modsDirectory, worldDirectory);
        this.creativeTabs = new CreativeTabRegistry();
        this.worldEvents = new WorldEvents();
    }

    public Logger logger(String modId) {
        return Logger.getLogger("LightLoader/" + Objects.requireNonNull(modId, "modId"));
    }

    public Instrumentation instrumentation() {
        return instrumentation;
    }

    public Chat chat() {
        return chat;
    }

    public CommandManager commands() {
        return commands;
    }

    public ModStorage storage() {
        return storage;
    }

    public CreativeTabRegistry creativeTabs() {
        return creativeTabs;
    }

    public WorldEvents worldEvents() {
        return worldEvents;
    }
}
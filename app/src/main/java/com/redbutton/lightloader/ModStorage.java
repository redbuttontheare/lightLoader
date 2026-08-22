package com.redbutton.lightloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class ModStorage {
    private final Path modsDirectory;
    private final Path worldDirectory;

    ModStorage(Path modsDirectory, Path worldDirectory) {
        this.modsDirectory = modsDirectory;
        this.worldDirectory = worldDirectory;
    }

    public Path modDirectory(String modId) {
        return create(modsDirectory.resolve(Objects.requireNonNull(modId, "modId")));
    }

    public Path worldDirectory(String modId) {
        return create(worldDirectory.resolve("lightloader").resolve(Objects.requireNonNull(modId, "modId")));
    }

    private static Path create(Path directory) {
        try {
            return Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create directory " + directory, exception);
        }
    }
}
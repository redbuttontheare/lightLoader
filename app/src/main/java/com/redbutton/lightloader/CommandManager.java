package com.redbutton.lightloader;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandManager {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public void register(Command command) {
        Objects.requireNonNull(command, "command");
        if (commands.putIfAbsent(command.name(), command) != null) {
            throw new IllegalArgumentException("Command already registered: " + command.name());
        }
    }

    public boolean execute(String input, Chat chat) {
        String[] tokens = input.trim().split("\\s+");
        if (tokens.length == 0 || tokens[0].isBlank()) {
            return false;
        }
        Command command = commands.get(tokens[0].toLowerCase());
        if (command == null) {
            return false;
        }
        command.execute(new CommandContext(Arrays.asList(tokens).subList(1, tokens.length), chat));
        return true;
    }
}
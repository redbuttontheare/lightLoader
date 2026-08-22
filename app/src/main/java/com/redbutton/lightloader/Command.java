package com.redbutton.lightloader;

public interface Command {
    String name();

    void execute(CommandContext context);
}
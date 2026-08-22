package com.redbutton.lightloader;

import java.util.List;

public record CommandContext(List<String> arguments, Chat chat) {
}
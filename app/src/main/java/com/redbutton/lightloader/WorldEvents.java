package com.redbutton.lightloader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class WorldEvents {
    private final List<WorldJoinListener> joinListeners = new CopyOnWriteArrayList<>();

    public void onJoin(WorldJoinListener listener) {
        joinListeners.add(listener);
    }

    public void fireJoin(String worldName) {
        for (WorldJoinListener listener : joinListeners) {
            listener.onWorldJoin(worldName);
        }
    }
}
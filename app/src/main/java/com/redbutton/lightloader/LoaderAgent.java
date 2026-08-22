package com.redbutton.lightloader;

import java.lang.instrument.Instrumentation;

public final class LoaderAgent {
    private LoaderAgent() {
    }

    public static void premain(String options, Instrumentation instrumentation) {
        LightLoader.launch(options, instrumentation);
    }

    public static void agentmain(String options, Instrumentation instrumentation) {
        LightLoader.launch(options, instrumentation);
    }
}
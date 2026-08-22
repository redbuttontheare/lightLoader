package com.redbutton.lightloader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class LightLoaderTest {
    @Test
    void discoversAndInitializesServiceProvider() {
        assertDoesNotThrow(() -> LightLoader.launch("test", null));
    }
}
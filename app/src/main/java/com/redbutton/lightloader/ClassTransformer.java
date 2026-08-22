package com.redbutton.lightloader;

public interface ClassTransformer {
    byte[] transform(String className, byte[] classfileBuffer);
}
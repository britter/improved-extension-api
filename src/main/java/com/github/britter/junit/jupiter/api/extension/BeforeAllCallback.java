package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface BeforeAllCallback extends org.junit.jupiter.api.extension.BeforeAllCallback {

    void beforeAll(TestClassContext context) throws Exception;

    @Override
    default void beforeAll(ExtensionContext context) throws Exception {
        beforeAll(new DefaultTestClassContext(context));
    }
}

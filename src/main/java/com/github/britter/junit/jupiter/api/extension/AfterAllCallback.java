package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface AfterAllCallback extends org.junit.jupiter.api.extension.AfterAllCallback {

    void afterAll(TestInstanceContext context) throws Exception;

    @Override
    default void afterAll(ExtensionContext context) throws Exception {

    }
}

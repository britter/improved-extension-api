package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface AfterEachCallback extends org.junit.jupiter.api.extension.AfterEachCallback {

    void afterEach(TestInstanceContext context) throws Exception;

    @Override
    default void afterEach(ExtensionContext context) throws Exception {
        afterEach(new DefaultTestInstanceContext(context));
    }
}

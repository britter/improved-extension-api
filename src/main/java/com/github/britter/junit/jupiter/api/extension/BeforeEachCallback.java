package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface BeforeEachCallback extends org.junit.jupiter.api.extension.BeforeEachCallback {

    void beforeEach(TestInstanceContext context) throws Exception;

    @Override
    default void beforeEach(ExtensionContext context) throws Exception {
        beforeEach(new DefaultTestInstanceContext(context));
    }
}

package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface TestInstancePostProcessor extends org.junit.jupiter.api.extension.TestInstancePostProcessor {

    void postProcessTestInstance(TestInstance testInstance, Context context) throws Exception;

    @Override
    default void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        postProcessTestInstance(
                new DefaultTestInstance(testInstance, new DefaultTestClass(testInstance.getClass())),
                new DefaultContext(context));
    }
}

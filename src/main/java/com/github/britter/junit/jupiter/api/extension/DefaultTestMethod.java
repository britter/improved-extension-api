package com.github.britter.junit.jupiter.api.extension;

import java.lang.reflect.Method;

public class DefaultTestMethod implements TestMethod {

    private Method testMethod;

    private TestInstance testInstance;

    private TestClass testClass;

    public DefaultTestMethod(Method testMethod, TestInstance testInstance, TestClass testClass) {
        this.testMethod = testMethod;
        this.testInstance = testInstance;
        this.testClass = testClass;
    }

    @Override
    public TestClass getTestClass() {
        return testClass;
    }

    @Override
    public TestInstance getTestInstance() {
        return testInstance;
    }

    @Override
    public Method getRawTestMethod() {
        return testMethod;
    }
}

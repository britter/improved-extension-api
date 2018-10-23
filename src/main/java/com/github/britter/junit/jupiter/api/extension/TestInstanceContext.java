package com.github.britter.junit.jupiter.api.extension;

public interface TestInstanceContext extends Context {

    TestClass getTestClass();

    TestInstance getTestInstance();

    TestMethod getTestMethod();
}

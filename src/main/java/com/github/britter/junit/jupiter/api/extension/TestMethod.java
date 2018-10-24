package com.github.britter.junit.jupiter.api.extension;

import java.lang.reflect.Method;

public interface TestMethod {

    TestClass getTestClass();

    TestInstance getTestInstance();

    Method getRawTestMethod();
}

package com.github.britter.junit.jupiter.api.extension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.TestInstance.Lifecycle;

public interface TestClass {

    Lifecycle getLifecycle();

    /**
     * Determine if this test class is a nested test class.
     *
     * @return {@code true} if this test class is nested.
     */
    boolean isNested();

    /**
     * Get the outer test class of this test class if available.
     *
     * <p>The outer test class is only set if this test class is a test class declared as a {@code @Nested}
     * test class.</p>
     *
     * @return the outer test class
     */
    Optional<TestClass> getOuterClass();

    /**
     * Get the outer most test class of this test class if available.
     *
     * <p>The outer most test class is only set if this test class is a test class declared as a {@code @Nested}
     * test class. The test class returned will be the top level test class that is not declared {@code @Nested}.</p>
     *
     * @return the outer most test class
     */
    Optional<TestClass> getOuterMostClass();

    /**
     * Get the chain of all outer test instances starting with the outer instance of this instance.
     *
     * @return the chain of outer test class
     */
    List<TestClass> getOuterClassChain();

    /**
     * Get the static fields declared in this class.
     *
     * @return the static fields declared in this class
     */
    Stream<TestField> getFields();

    /**
     * Get the static fields from this class and all outer classes
     *
     * @return the static fields declared in this class and all outer classes
     */
    Stream<TestField> getFieldsFromHierachy();

    Class<?> getRawTestClass();
}

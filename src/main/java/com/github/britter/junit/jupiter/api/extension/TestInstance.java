package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// TODO does it make sense to have this extend TestElement?
public interface TestInstance {

    Lifecycle getLifecycle();

    /**
     * Determine if this test class is a nested test class.
     *
     * @return {@code true} if this test class is nested.
     */
    boolean isNested();

    /**
     * Get the outer test instance of this test instance if available.
     *
     * <p>The outer test instance is only set if this test instance is a test instance declared as a {@code @Nested}
     * test class.</p>
     *
     * @return the outer test instance
     */
    Optional<TestInstance> getOuterInstance();

    /**
     * Get the outer most test instance of this test instance if available.
     *
     * <p>The outer most test instance is only set if this test instance is a test instance declared as a {@code @Nested}
     * test class. The test instance returned will be the top level instance of a test class that is not declared
     * {@code @Nested}.</p>
     *
     * @return the outer most test instance
     */
    Optional<TestInstance> getOuterMostInstance();

    /**
     * Get the chain of all outer test instances starting with the outer instance of this instance.
     *
     * @return the chain of outer test instances
     */
    List<TestInstance> getOuterInstanceChain();

    /**
     * Get the instance fields declared in the class of this test instance.
     *
     * @return the instance fields declared in the class of this test instance
     */
    Stream<TestField> getFields();

    /**
     * Get the instance fields from the class of this test instance and all outer test instances.
     *
     * @return the instance fields declared in the class of this test instance and all outer test instances.
     */
    Stream<TestField> getFieldsFromHierachy();

    Object getRawTestInstance();
}

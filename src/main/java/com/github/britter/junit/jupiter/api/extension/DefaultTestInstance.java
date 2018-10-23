package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

class DefaultTestInstance implements TestInstance {

    private Object testInstance;

    private TestClass testClass;

    DefaultTestInstance(Object testInstance, TestClass testClass) {
        this.testInstance = testInstance;
        this.testClass = testClass;
    }

    @Override
    public Lifecycle getLifecycle() {
        return testClass.getLifecycle();
    }

    @Override
    public boolean isNested() {
        return testClass.isNested();
    }

    @Override
    public Optional<TestInstance> getOuterInstance() {
        return getOuterInstance(testInstance)
                .map(instance -> new DefaultTestInstance(instance, new DefaultTestClass(instance.getClass())));
    }

    @Override
    public Optional<TestInstance> getOuterMostInstance() {
        List<TestInstance> chain = getOuterInstanceChain();
        return chain.isEmpty() ? Optional.empty() : Optional.of(chain.get(chain.size() - 1));
    }

    @Override
    public List<TestInstance> getOuterInstanceChain() {
        List<TestInstance> outerInstances = new ArrayList<>();
        Optional<TestInstance> outerInstance = getOuterInstance();
        while(outerInstance.isPresent()) {
            outerInstances.add(outerInstance.get());
            outerInstance = outerInstance.flatMap(TestInstance::getOuterInstance);
        }
        return outerInstances;
    }

    @Override
    public Stream<TestField> getFields() {
        return Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(ReflectionUtils::isNotStatic)
                .map(f -> new DefaultTestField(f, testInstance));
    }

    @Override
    public Stream<TestField> getFieldsFromHierachy() {
        return getOuterInstanceChain().stream().flatMap(TestInstance::getFields);
    }

    @Override
    public Object getRawTestInstance() {
        return testInstance;
    }

    // copied from ReflectionUtils
    private static Optional<Object> getOuterInstance(Object inner) {
        // This is risky since it depends on the name of the field which is nowhere guaranteed
        // but has been stable so far in all JDKs

        // @formatter:off
        return Arrays.stream(inner.getClass().getDeclaredFields())
                .filter(field -> field.getName().startsWith("this$"))
                .findFirst()
                .map(field -> {
                    try {
                        return ReflectionUtils.makeAccessible(field).get(inner);
                    }
                    catch (Throwable t) {
                        throw ExceptionUtils.throwAsUncheckedException(t);
                    }
                });
        // @formatter:on
    }
}

package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.TestInstance.Lifecycle;

class DefaultTestClass implements TestClass {

    private Class<?> testClass;

    DefaultTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

    @Override
    public Lifecycle getLifecycle() {
        // TODO lifecycle is inherited from parent class
        return AnnotationSupport
                .findAnnotation(testClass, TestInstance.class)
                .map(TestInstance::value)
                .orElse(Lifecycle.PER_METHOD);
    }

    @Override
    public boolean isNested() {
        return AnnotationSupport.isAnnotated(testClass, Nested.class);
    }

    @Override
    public Optional<TestClass> getOuterClass() {
        return Optional.ofNullable(testClass.getEnclosingClass()).map(DefaultTestClass::new);
    }

    @Override
    public Optional<TestClass> getOuterMostClass() {
        List<TestClass> chain = getOuterClassChain();
        return chain.isEmpty() ? Optional.empty() : Optional.of(chain.get(chain.size() - 1));
    }

    @Override
    public List<TestClass> getOuterClassChain() {
        List<TestClass> outerClasses = new ArrayList<>();
        Optional<TestClass> outerClass = getOuterClass();
        while(outerClass.isPresent()) {
            outerClasses.add(outerClass.get());
            outerClass = outerClass.flatMap(TestClass::getOuterClass);
        }
        return outerClasses;
    }

    @Override
    public Stream<TestField> getFields() {
        return Arrays.stream(testClass.getDeclaredFields())
                .filter(ReflectionUtils::isStatic)
                .map(StaticField::new);
    }

    @Override
    public Stream<TestField> getFieldsFromHierachy() {
        // TODO does this work? To get the field value we probably need the class where the field is defined...
        return getOuterClassChain().stream().flatMap(TestClass::getFields);
    }

    @Override
    public Class<?> getRawTestClass() {
        return testClass;
    }
}

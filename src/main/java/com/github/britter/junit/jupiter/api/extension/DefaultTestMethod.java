package com.github.britter.junit.jupiter.api.extension;

import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

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

    @Override
    public String getName() {
        return testMethod.getName();
    }

    @Override
    public boolean hasName(String name) {
        return getName().equals(name);
    }

    @Override
    public boolean isAnnotated(Class<? extends Annotation> annotation) {
        return AnnotationSupport.isAnnotated(testMethod, annotation);
    }

    @Override
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationType) {
        return AnnotationSupport.findAnnotation(testMethod, annotationType);
    }

    @Override
    public <A extends Annotation> Optional<A> getAnnotationFromHierachy(Class<A> annotationType) {
        return getAnnotation(annotationType).isPresent() ? getAnnotation(annotationType) : testClass.getAnnotationFromHierachy(annotationType);
    }
}

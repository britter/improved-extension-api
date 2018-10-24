package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.annotation.Annotation;
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
        Stream<TestClass> chain = getOuterClassHierarchy();
        return chain.reduce((first, second) -> second);
    }

    @Override
    public Stream<TestClass> getOuterClassHierarchy() {
        List<TestClass> outerClasses = new ArrayList<>();
        Optional<TestClass> outerClass = getOuterClass();
        while(outerClass.isPresent()) {
            outerClasses.add(outerClass.get());
            outerClass = outerClass.flatMap(TestClass::getOuterClass);
        }
        return outerClasses.stream();
    }

    @Override
    public Stream<TestField> getFields() {
        return Arrays.stream(testClass.getDeclaredFields())
                .filter(ReflectionUtils::isStatic)
                .map(f -> DefaultTestField.staticField(f, getRawTestClass()));
    }

    @Override
    public Stream<TestField> getFieldsFromHierachy() {
        // TODO does this work? To get the field value we probably need the class where the field is defined...
        return getOuterClassHierarchy().flatMap(TestClass::getFields);
    }

    @Override
    public Class<?> getRawTestClass() {
        return testClass;
    }

    @Override
    public String getName() {
        return testClass.getName();
    }

    @Override
    public boolean hasName(String name) {
        return getName().equals(name);
    }

    @Override
    public boolean isAnnotated(Class<? extends Annotation> annotation) {
        return AnnotationSupport.isAnnotated(testClass, annotation);
    }

    @Override
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationType) {
        return AnnotationSupport.findAnnotation(testClass, annotationType);
    }

    @Override
    public <A extends Annotation> Optional<A> getAnnotationFromHierachy(Class<A> annotationType) {
        return getAnnotation(annotationType).isPresent()
                ? getAnnotation(annotationType)
                : getAnnotationFromOuterClassChain(annotationType);
    }

    private <A extends Annotation> Optional<A> getAnnotationFromOuterClassChain(Class<A> annotationType) {
        // FIXME could be simplified using Java 9 Optional::stream
        return getOuterClassHierarchy()
                .map(clazz -> clazz.getAnnotation(annotationType))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}

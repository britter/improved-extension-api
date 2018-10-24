package com.github.britter.junit.jupiter.api.extension;

import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Optional;

class DefaultTestField implements TestField {

    private final Field testField;

    private final Object host;

    private DefaultTestField(Field testField, Object host) {
        this.testField = testField;
        this.host = host;
    }

    static TestField staticField(Field testField, Class<?> clazz) {
        return new DefaultTestField(testField, clazz);
    }

    static TestField instanceField(Field testField, Object testInstance) {
        return new DefaultTestField(testField, testInstance);
    }

    @Override
    public String getName() {
        return testField.getName();
    }

    @Override
    public boolean hasName(String name) {
        return getName().equals(name);
    }

    @Override
    public boolean hasValue() {
        return getValue().isPresent();
    }

    @Override
    public <T> Optional<T> getValue() {
        // TODO check type and throw exception?
        return (Optional<T>) ReflectionUtils.readFieldValue(testField, host);
    }

    @Override
    public <T> boolean hasType(Class<T> type) {
        return type.isAssignableFrom(testField.getType());
    }

    @Override
    public boolean isAnnotated(Class<? extends Annotation> annotation) {
        return AnnotationSupport.isAnnotated(testField, annotation);
    }

    @Override
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationType) {
        return AnnotationSupport.findAnnotation(testField, annotationType);
    }

    @Override
    public <A extends Annotation> Optional<A> getAnnotationFromHierachy(Class<A> annotationType) {
        // TODO need to have access to the containing TestClass
        return Optional.empty();
    }

    @Override
    public boolean isPublic() {
        return ReflectionUtils.isPublic(testField);
    }

    @Override
    public boolean isProtected() {
        return Modifier.isProtected(((Member) testField).getModifiers());
    }

    @Override
    public boolean isPrivate() {
        return ReflectionUtils.isPrivate(testField);
    }

    @Override
    public boolean isStatic() {
        return ReflectionUtils.isStatic(testField);
    }

    @Override
    public Field getRawField() {
        return testField;
    }
}

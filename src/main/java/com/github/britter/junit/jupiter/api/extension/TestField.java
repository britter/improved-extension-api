package com.github.britter.junit.jupiter.api.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public interface TestField {

    String getName();

    boolean hasName(String name);

    boolean hasValue();

    <T> Optional<T> getValue();

    <T> boolean hasType(Class<T> type);

    boolean isAnnotated(Class<? extends Annotation> annotation);

    boolean isPublic();

    boolean isProtected();

    boolean isPrivate();

    boolean isStatic();

    Field getRawField();
}

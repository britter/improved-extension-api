package com.github.britter.junit.jupiter.api.extension;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * Some utils for making it easier to work with TestFields
 */
public final class TestFields {

    private TestFields() {
    }

    public static <T> Predicate<TestField> hasType(Class<T> type) {
        return f -> f.hasType(type);
    }

    public static Predicate<TestField> isAnnotated(Class<? extends Annotation> annotation) {
        return f -> f.isAnnotated(annotation);
    }
}

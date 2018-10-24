package com.github.britter.junit.jupiter.api.extension;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface TestElement {

    String getName();

    boolean hasName(String name);

    boolean isAnnotated(Class<? extends Annotation> annotation);

    <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationType);

    // TODO would be useful to declare the traversal mode
    <A extends Annotation> Optional<A> getAnnotationFromHierachy(Class<A> annotationType);

}

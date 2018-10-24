package com.github.britter.sample.testcontainers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code @Container} annotation is used in conjunction with the {@link Testcontainers} annotation
 * to mark containers that should be managed by the Testcontainers extension.
 *
 * @see Testcontainers
 * @since 1.10.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Container {
}

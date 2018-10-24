package com.github.britter.sample.testcontainers;

import com.github.britter.junit.jupiter.api.extension.BeforeAllCallback;
import com.github.britter.junit.jupiter.api.extension.BeforeEachCallback;
import com.github.britter.junit.jupiter.api.extension.TestClassContext;
import com.github.britter.junit.jupiter.api.extension.TestField;
import com.github.britter.junit.jupiter.api.extension.TestInstanceContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.testcontainers.lifecycle.Startable;

import java.util.stream.Stream;

import static com.github.britter.junit.jupiter.api.extension.TestFields.hasType;
import static com.github.britter.junit.jupiter.api.extension.TestFields.isAnnotated;

class TestcontainersExtension implements BeforeAllCallback, BeforeEachCallback {

    private static final Namespace NAMESPACE = Namespace.create(TestcontainersExtension.class);


    @Override
    public void beforeAll(TestClassContext context) {
        findAndStartContainers(context.getTestClass().getFieldsFromHierachy(), context.getStore(NAMESPACE));
    }

    @Override
    public void beforeEach(final TestInstanceContext context) {
        findAndStartContainers(context.getTestInstance().getFieldsFromHierachy(), context.getStore(NAMESPACE));
    }

    private static void findAndStartContainers(Stream<TestField> fields, ExtensionContext.Store store) {
        fields.filter(hasType(Startable.class))
                .filter(isAnnotated(Container.class))
                .map(field -> new StoreAdapter(field.getRawField().getDeclaringClass(), field.getName(), field.<Startable>getValue().get()))
                .forEach(adapter -> store
                        .getOrComputeIfAbsent(adapter.key, k -> adapter.start()));
    }

    /**
     * An adapter for {@link Startable} that implement {@link CloseableResource}
     * thereby letting the JUnit automatically stop containers once the current
     * {@link ExtensionContext} is closed.
     */
    private static class StoreAdapter implements CloseableResource {

        private String key;

        private Startable container;

        private StoreAdapter(Class<?> declaringClass, String fieldName, Startable container) {
            this.key = declaringClass.getName() + "." + fieldName;
            this.container = container;
        }

        private StoreAdapter start() {
            container.start();
            return this;
        }

        @Override
        public void close() {
            container.stop();
        }
    }
}

package com.github.britter.sample.testcontainers;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;
import org.testcontainers.lifecycle.Startable;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

class TestcontainersExtensionOriginal implements TestInstancePostProcessor, BeforeEachCallback {

    private static final Namespace NAMESPACE = Namespace.create(TestcontainersExtensionOriginal.class);

    private static final String TEST_INSTANCE = "testInstance";

    @Override
    public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) {
        ExtensionContext.Store store = context.getStore(NAMESPACE);
        store.put(TEST_INSTANCE, testInstance);

        findSharedContainers(testInstance)
            .forEach(adapter -> store.getOrComputeIfAbsent(adapter.key, k -> adapter.start()));
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        collectParentTestInstances(context)
            .stream()
            .flatMap(this::findRestartedContainers)
            .forEach(adapter -> context.getStore(NAMESPACE)
                .getOrComputeIfAbsent(adapter.key, k -> adapter.start()));
    }

    private Set<Object> collectParentTestInstances(final ExtensionContext context) {
        Set<Object> testInstances = new LinkedHashSet<>();
        Optional<ExtensionContext> current = Optional.of(context);
        while(current.isPresent()) {
            ExtensionContext ctx = current.get();
            Object testInstance = ctx.getStore(NAMESPACE).remove(TEST_INSTANCE);
            if (testInstance != null) {
                testInstances.add(testInstance);
            }
            current = ctx.getParent();
        }
        return testInstances;
    }

    private Stream<StoreAdapter> findSharedContainers(Object testInstance) {
        return ReflectionUtils.findFields(
                testInstance.getClass(),
                isSharedContainer(),
                ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
            .stream()
            .map(f -> getContainerInstance(testInstance, f));
    }

    private Predicate<Field> isSharedContainer() {
        return isContainer().and(ReflectionUtils::isStatic);
    }

    private Stream<StoreAdapter> findRestartedContainers(Object testInstance) {
        return ReflectionUtils.findFields(
                testInstance.getClass(),
                isRestartContainer(),
                ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
            .stream()
            .map(f -> getContainerInstance(testInstance, f));
    }

    private Predicate<Field> isRestartContainer() {
        return isContainer().and(ReflectionUtils::isNotStatic);
    }

    private static Predicate<Field> isContainer() {
        return field -> Startable.class.isAssignableFrom(field.getType()) && AnnotationSupport.isAnnotated(field, Container.class);
    }

    private static StoreAdapter getContainerInstance(final Object testInstance, final Field field) {
        try {
            field.setAccessible(true);
            Startable containerInstance = Preconditions.notNull((Startable) field.get(testInstance), "Container " + field.getName() + " needs to be initialized");
            return new StoreAdapter(field.getDeclaringClass(), field.getName(), containerInstance);
        } catch (IllegalAccessException e) {
            throw new ExtensionConfigurationException("Can not access container defined in field " + field.getName());
        }
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

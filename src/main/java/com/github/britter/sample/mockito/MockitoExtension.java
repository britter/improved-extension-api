/*
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package com.github.britter.sample.mockito;


import com.github.britter.junit.jupiter.api.extension.AfterEachCallback;
import com.github.britter.junit.jupiter.api.extension.BeforeEachCallback;
import com.github.britter.junit.jupiter.api.extension.TestInstanceContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.internal.configuration.MockAnnotationProcessor;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.quality.Strictness;

import java.lang.reflect.Parameter;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

/**
 * Extension that initializes mocks and handles strict stubbings. This extension is the JUnit Jupiter equivalent
 * of our JUnit4 {@link MockitoJUnitRunner}.
 *
 * Example usage:
 *
 * <pre class="code"><code class="java">
 * <b>&#064;ExtendWith(MockitoExtension.class)</b>
 * public class ExampleTest {
 *
 *     &#064;Mock
 *     private List&lt;Integer&gt; list;
 *
 *     &#064;Test
 *     public void shouldDoSomething() {
 *         list.add(100);
 *     }
 * }
 * </code></pre>
 *
 * If you would like to configure the used strictness for the test class, use {@link MockitoSettings}.
 *
 * <pre class="code"><code class="java">
 * <b>&#064;MockitoSettings(strictness = Strictness.STRICT_STUBS)</b>
 * public class ExampleTest {
 *
 *     &#064;Mock
 *     private List&lt;Integer&gt; list;
 *
 *     &#064;Test
 *     public void shouldDoSomething() {
 *         list.add(100);
 *     }
 * }
 * </code></pre>
 *
 * This extension also supports JUnit Jupiter's method parameters.
 * Use parameters for initialization of mocks that you use only in that specific test method.
 * In other words, where you would initialize local mocks in JUnit 4 by calling {@link Mockito#mock(Class)},
 * use the method parameter. This is especially beneficial when initializing a mock with generics, as you no
 * longer get a warning about "Unchecked assignment".
 * Please refer to JUnit Jupiter's documentation to learn when method parameters are useful.
 *
 * <pre class="code"><code class="java">
 * <b>&#064;ExtendWith(MockitoExtension.class)</b>
 * public class ExampleTest {
 *
 *     &#064;Mock
 *     private List&lt;Integer&gt; sharedList;
 *
 *     &#064;Test
 *     public void shouldDoSomething() {
 *         sharedList.add(100);
 *     }
 *
 *     &#064;Test
 *     public void hasLocalMockInThisTest(@Mock List&lt;Integer&gt; localList) {
 *         localList.add(100);
 *         sharedList.add(100);
 *     }
 * }
 * </code></pre>
 *
 * Lastly, the extension supports JUnit Jupiter's constructor parameters.
 * This allows you to do setup work in the constructor and set
 * your fields to <code>final</code>.
 * Please refer to JUnit Jupiter's documentation to learn when constructor parameters are useful.
 *
 * <pre class="code"><code class="java">
 * <b>&#064;ExtendWith(MockitoExtension.class)</b>
 * public class ExampleTest {
 *
 *      private final List&lt;Integer&gt; sharedList;
 *
 *      ExampleTest(&#064;Mock sharedList) {
 *          this.sharedList = sharedList;
 *      }
 *
 *      &#064;Test
 *      public void shouldDoSomething() {
 *          sharedList.add(100);
 *      }
 * }
 * </code></pre>
 */
public class MockitoExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private final static Namespace MOCKITO = create("org.mockito");

    private final static String SESSION = "session";

    private final Strictness strictness;

    // This constructor is invoked by JUnit Jupiter via reflection or ServiceLoader
    @SuppressWarnings("unused")
    public MockitoExtension() {
        this(Strictness.STRICT_STUBS);
    }

    private MockitoExtension(Strictness strictness) {
        this.strictness = strictness;
    }

    /**
     * Callback that is invoked <em>before</em> each test is invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void beforeEach(final TestInstanceContext context) {
        Set<Object> testInstances = new LinkedHashSet<>();
        testInstances.add(context.getTestInstance().getRawTestInstance());
        testInstances.addAll(context.getTestInstance().getOuterInstanceChain());

        Strictness actualStrictness = context.getTestClass().getAnnotationFromHierachy(MockitoSettings.class)
                .map(MockitoSettings::strictness)
                .orElse(strictness);

        MockitoSession session = Mockito.mockitoSession()
                .initMocks(testInstances.toArray())
                .strictness(actualStrictness)
                .startMocking();

        context.getStore(MOCKITO).put(SESSION, session);
    }

    /**
     * Callback that is invoked <em>after</em> each test has been invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void afterEach(TestInstanceContext context) {
        context.getStore(MOCKITO).remove(SESSION, MockitoSession.class)
                .finishMocking();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(Mock.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        final Parameter parameter = parameterContext.getParameter();
        return MockAnnotationProcessor.processAnnotationForMock(parameterContext.findAnnotation(Mock.class).get(), parameter.getType(), parameter.getName());
    }
}

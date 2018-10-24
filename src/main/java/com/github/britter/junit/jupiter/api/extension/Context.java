package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface Context {

    Optional<Context> getParent();

    Context getRoot();

    String getUniqueId();

    String getDisplayName();

    Set<String> getTags();

    Optional<Throwable> getExecutionException();

    Optional<String> getConfigurationParameter(String key);

    void publishReportEntry(Map<String, String> map);

    default void publishReportEntry(String key, String value) {
        this.publishReportEntry(Collections.singletonMap(key, value));
    }

    default void publishReportEntry(String value) {
        this.publishReportEntry("value", value);
    }

    ExtensionContext.Store getStore(ExtensionContext.Namespace namespace);
}

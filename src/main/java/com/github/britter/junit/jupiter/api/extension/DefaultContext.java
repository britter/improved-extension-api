package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

class DefaultContext implements Context {

    ExtensionContext ctx;

    public DefaultContext(ExtensionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Optional<Context> getParent() {
        // TODO create correct instance here
        return ctx.getParent().map(DefaultContext::new);
    }

    @Override
    public Context getRoot() {
        return new DefaultContext(ctx.getRoot());
    }

    @Override
    public String getUniqueId() {
        return ctx.getUniqueId();
    }

    @Override
    public String getDisplayName() {
        return ctx.getDisplayName();
    }

    @Override
    public Set<String> getTags() {
        return ctx.getTags();
    }

    @Override
    public Optional<Throwable> getExecutionException() {
        return ctx.getExecutionException();
    }

    @Override
    public Optional<String> getConfigurationParameter(String key) {
        return ctx.getConfigurationParameter(key);
    }

    @Override
    public void publishReportEntry(Map<String, String> map) {
        ctx.publishReportEntry(map);
    }

    @Override
    public ExtensionContext.Store getStore(ExtensionContext.Namespace namespace) {
        return ctx.getStore(namespace);
    }

}

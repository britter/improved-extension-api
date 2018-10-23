package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

class DefaultTestClassContext extends DefaultContext implements TestClassContext {

    DefaultTestClassContext(ExtensionContext ctx) {
        super(ctx);
    }

    @Override
    public TestClass getTestClass() {
        return new DefaultTestClass(ctx.getRequiredTestClass());
    }
}

package com.github.britter.junit.jupiter.api.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

class DefaultTestInstanceContext extends DefaultContext implements TestInstanceContext {

    DefaultTestInstanceContext(ExtensionContext ctx) {
        super(ctx);
    }

    @Override
    public TestClass getTestClass() {
        return new DefaultTestClass(ctx.getRequiredTestClass());
    }

    @Override
    public TestInstance getTestInstance() {
        return new DefaultTestInstance(ctx.getRequiredTestInstance(), getTestClass());
    }

    @Override
    public TestMethod getTestMethod() {
        return new DefaultTestMethod(ctx.getRequiredTestMethod(), getTestInstance(), getTestClass());
    }
}

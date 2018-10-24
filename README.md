# improved-extension-api

This repository contains a proposal for an improved JUnit Jupiter extension API (see [junit-team/junit5#1611](https://github.com/junit-team/junit5/issues/1611)).

## Introduction

The API proposal is implemented in the `com.github.britter.junit.jupiter.extension` package.
The implementation defines subinterfaces for the callback interfaces of the JUnit Jupiter extension API.
The callback methods of e.g. `BeforeEachCallback` are implemented using default methods which pass wrapper objects to the new callback methods:

```java
public interface BeforeEachCallback extends org.junit.jupiter.api.extension.BeforeEachCallback {

    void beforeEach(TestInstanceContext context) throws Exception;

    @Override
    default void beforeEach(ExtensionContext context) throws Exception {
        beforeEach(new DefaultTestInstanceContext(context));
    }
}
```

This way the API could be used alongside the existing API.

## Goals

Goals of the proposal are:

- easy to discover for extension authors, e.g. no need to know about `Util/Support` classes
- Object-oriented design with meaningful objects from the domain of testing
- implement common tasks which are useful when implementing extensions, e.g.: find annotated fields of a certain type, access outer test instances/classes
- hide the details of the raw reflection API
- Follow [ISP](https://en.wikipedia.org/wiki/Interface_segregation_principle), for example it does not make sense to provide access to a test method in the `BeforeAllCallback`

## Samples

The repository contains two usage examples for the API proposal:

1. Testcontainers extension copied from the [corresponding PR](https://github.com/testcontainers/testcontainers-java/pull/887) in package `com.github.britter.sample.testcontainers`
2. Mockito extension copied from the [Mockito project](https://github.com/mockito/mockito/tree/release/2.x/subprojects/junit-jupiter/src/main/java/org/mockito/junit/jupiter) in package `com.github.britter.sample.mockito`

Both samples contain the original implementation for easier comparision.

## Status

**Note:** The state of this API is EXPERIMENTAL! It's not tested and not ready for use.
It is only partially implemented, for example the whole `TestMethod` API is missing.

## Contribution policy

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

## License

This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).

The code in the `com.github.britter.sample.mockito` package has been copied from the [Mockito](https://github.com/mockito/mockito) project and ist licensed under the [MIT License](https://opensource.org/licenses/MIT). 

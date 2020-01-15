# DI library/framework to simplify development

* Status: accepted

## Context and Problem Statement

How to construct all objects required for application to run correctly?
How to manage dependencies between objects?

## Decision Drivers

* fast
* will not bloat the code with framework approach

## Considered Options

* spring
* guice

## Decision Outcome

Chosen option: guice, because it's fast, lightweight and allows to test how DI works with library instead of framework.

I've decided to use @Inject to simplify object creation and declare all dependencies on the constructor.

### Positive Consequences

* Application startup time will be fast
* each module will have clearly defined boundaries 

### Negative Consequences

* it offers nothing more than DI library

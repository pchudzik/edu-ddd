# What build tool to use

* Status: accepted

## Context and Problem Statement

Need build tool to fetch dependencies, compile project, run tests.

## Decision Drivers <!-- optional -->

* easy to use

## Considered Options

* gradle
* maven

## Decision Outcome

Chosen option: maven, because:
* Easy to add modules
* Don't expect any deployments
* Don't expect any customization 

## Pros and Cons of the Options

### gradle

* Good, because it's very flexible
* Good, because you can do anything with it
* Bad, because I don't have any use cases that might explain gradle usage

### maven

* Good, because it's easy to add modules, nested modules, and experiment with things
* Good, because it figures out dependencies between modules on it's own
* Bad, because it's very verbose (XML)

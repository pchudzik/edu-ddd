# Avoiding primitives

* Status: accepted

## Context and Problem Statement

Should things like object id's, text field values etc be objects or should they be primitives?

## Considered Options

* primitives
* objects all the way
* mix it

## Decision Outcome

Chosen option: "mix it", because sounds the most reasonable and will allow to postpone decision in time and make it depending on conditions.

Prefer primitives (or native java classes) when:
* they are hidden and never exposed to other parts of domain
* no one knows they exists and they are simply implementation detail

Prefer objects when:
* objects are passed as method arguments
* objects are part of public API
* additional validations must be performed on domain and can be done inside such object

## Pros and Cons of the Options

### primitives

* Good, because it's fast for starters
* Bad, because might get harder to understand in long term

### objects all the way

* Good, because compiler will help out with keeping everything organized
* Good, because it's more future proof
* Good, because allows to easily introduce domain validation
* Bad, because a lot of possibly unnecessary code will be written 

### mix it

* Good, because it is a mix of what's good from both approaches
* Bad, because it is a mix of what's bad from both approaches

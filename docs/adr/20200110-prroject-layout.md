# How to organize project modules

* Status: accepted

## Context and Problem Statement

How to organize project modules. Where public interfaces should be put, where implementation should be located.

## Decision Drivers

* easy to develop
* easy to keep structure
* easy to navigate
* conventions are easily sustained

## Considered Options

* module per BC with separate API module per BC
* monolith approach with package visibility
* bounded context per project

## Decision Outcome

Chosen option: "monolith approach with package visibility", because seems like the simples way to get started.
With archunit to enforce conventions sounds reasonable. 

### Positive Consequences

* faster initial development
* easy to test and run communication between BC's

### Negative Consequences

* monolith approach will require extra care to make sure structure is maintained
* hard to split in the future
* tests will be written to guarantee architectural choices 

## Pros and Cons of the Options

### module per BC with separate API module per BC

Each bounded context is separate maven project.
When Bounded context is created put it in new maven module so it's separated from other BC's.
Public API of the module should be put in separate module by default to separate what's public available and what's not.  

```
.
|-- pom.xml
|-- projects
|   |-- pomo.xml
|   |-- project-api
|   |   |-- pom.xml
|   |   |-- src/
|   |--- project-impl
|   |    |   |-- pom.xml
|   |    |   |-- src/
|-- issues
    |-- pom.xml
    |-- issues-api
    |   |-- pom.xml
    |   |-- src/
    |--- issues-impl
             |-- pom.xml
             |-- src/
```

* Good, because everything is clean and it's hard to break it
* Good, because there is clear separation of bounded context's and modules responsibility 
* Bad, because a lot of maven modules will be created
* Bad, because it'll be hard to communicate between BC's
* Bad, because not everything must be separate microservice
* Bad, because in order to communicate between BC's rest API's must be created or uber module which will have everything inside

### monolith approach with package visibility

Drop everything inside single module.
Add archunit to the project to force desired architecture.

* Good, it's simple and easy to work with
* Good, it's easy to communicate between BCs
* Good, project is not bloated with many modules
* Bad, because it'll be harder to split project into modules in the future

### bounded context per project

Each bounded context is separate maven project.
When Bounded context is created put it in new maven module so it's separated from other BC's
Public API of the module should will put in the same module. 

```
.
|-- pom.xml
|-- projects
|   |-- pomo.xml
|   |-- src/
|-- issues
    |-- pom.xml
    |-- pom.xml
   |-- src/
```

* Good, because everything is relatively clean and it's hard to break it
* Good, because there is clear separation of bounded context's 
* Bad, because archunit must be used to enforce conventions inside module
* Bad, because a lot of maven modules will be created
* Bad, because it'll be hard to communicate between BC's
* Bad, because not everything must be separate microservice
* Bad, because in order to communicate between BC's rest API's must be created or uber module which will have everything inside

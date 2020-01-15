# Storage to easily share state between BC's 

* Status: accepted

## Context and Problem Statement

How to share the data between BC's in a way that will allow to easily implement multiple views on the same object?
How to share data between BC easily and allow to easily communicate between BC?

## Decision Drivers

* easy to use
* fast to setup
* light weight
* will not change the focus of the project

## Considered Options

* in memory java based repositories
* h2 + flyway + jpa (hibernate)
* h2 + flyway + jdbc
* h2 + flyway + jdbi
* h2 + flyway + jooq

## Decision Outcome

Chosen option: h2 + flyway + jdbi, because:
* it matches most of drivers (easy to use, fast to setup, lightweight)
* will allow to learn new database access technology along the way (which is similar to jdbc)
* allow to introduce further communication mechanisms based on selected choice

### Positive Consequences

* it's lightweight and everything is in memory

### Negative Consequences

* need to write sql by hand
* need to write tests to ensure sqls are working (no compile time checks)

## Pros and Cons of the Options

### in memory java based repositories

Write repositories to store objects by hand and implement all required operations manually

* Good, because will be very fast
* Bad, because a lot of code will be produced
* Bad, because it'll be hard to implement different views on the same objects
* Bad, because it'll be easy to introduce bugs when using it

### h2 + flyway + jpa (hibernate)

* Good, because it's easy to write sql in it
* Good, because it checks generated sql
* Good, because allows to easily create CRUD
* Bad, because it's not lightweight because of heavy classpath scanning and startup overhead
* Bad, because I don't want to create a lot of CRUDs and I'd like to focus on more complicated bits

### h2 + flyway + (jdbc|jdbi|jooq)

* Good, because it's very light
* Good, because it's easy to setup
* Good, because I understand the idea behind
* Good, because flyway will manage data migration and will allow me to tests introducing backward compatible changes (more real life)
* Bad, because will have to write sql by hand
* Bad, because will have to test produced sql 

# Messaging solution

* Status: accepted

## Context and Problem Statement

What technology to use to pass messages between BC?

## Decision Drivers

* simple and fast

## Considered Options

* guava eventbus
* JMS (rabbit or activemq)
* self written sql based solution

## Decision Outcome

Chosen option: guava eventbus, because it's fast and easy to use

### Positive Consequences

* faster development not focused on technicalities

### Negative Consequences

* listeners will be annotated with guava's @Subscribe annotation

## Pros and Cons of the Options

### guava eventbus

* Good, because it's easy to use
* Good, because it's fast
* Bad, because it is not transactional
* Bad, because messages will be lost after shutdown

### JMS

Possible solutions:
* ActiveMQ
* RabbitMQ
* ZeroMQ

* Good, because fully operational JMS standard and realiable solution
* Bad, because it's slow
* Bad, because I'll go to much into technicalities

### self written sql based solution

Like database table with all events and some mechanism to poll for those events and ensure they are delivered and handled properly.

* Good, because it's transactional
* Bad, because will have to write it
* Bad, because it's not main goal of this project

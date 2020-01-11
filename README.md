# EDU-DDD

## Context

Sample bug tracking project meant to learn and practice DDD approach to software development.
It's not meant to resolve any real life problems it'll serve as purely educational project.

## Scope

### Included

* Core domain implementation
  * Walking skeleton in the first iteration will include:
    * Project because every big organization have many of them 
    * Issues inside project (title, description)
    * Users just simple name and nothing more for now
  * Acceptance tests for those scenarios
* Future "unexpected" changes
  * To simulate real life and project evolution in time and check how assumptions will work
  * New features will be added without much planning up front.
    I'm not going to spend too much time thinking now about what possible features will be implemented.
    I'll start with walking skeleton and then try to add new features depending on current star alignments and air humidity.
* Tests unit and acceptance
  * Verify how hexagonal architecture works with testing
  * Check by myself how DDD approach works with testing and TDD
* CI because I tend not to run all tests so it's just precaution to be sure it compiles ;)
* Documentation
  * To see how iterative process and postponing everything to the last possible moment will work out in a long run
  * Practice writing ADR
  * Practice in using plantuml (when applicable) 
* Modules and project layout
  * What will be the best project layout that will support splitting monolith in the future
  * What project layout will support and help in keeping conventions
  * How project and modules layout will affect future requests

### Excluded (for now)

* REST API 
* Database
* Any framework integration (what about IC and DI?)

## Building

* Requires java 13

`./mvnw clean package`

## Changelog

### 2020-01-11
* Added initial code for Project, Issue and User - no logic yet, just simple classes available for later development. 

### 2020-01-07
* Project skeleton with basic CI configured
* Added readme, documented already made decisions
* Decided on the scope and on the approach to the project development

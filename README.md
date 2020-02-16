# EDU-DDD

## Context

Sample bug tracking project meant to learn and practice DDD approach to software development.
It's not meant to resolve any real life problems it'll serve as purely educational project.
There are no external dependencies to this project.
All what's required is served as in memory services.

## Functional overview

### Included

* Core domain implementation
  * Walking skeleton of some features
  * Acceptance tests for those scenarios
* Future "unexpected" changes
  * To simulate real life and project evolution in time and check how assumptions will work
  * New features will be added without much planning up front.
    I'm not going to spend too much time thinking now about what possible features will be implemented.
    I'll start with walking skeleton and then try to add new features depending on current star alignments and air humidity.
* Unit and acceptance tests
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

### Unexpected changes

#### Dynamic fields [18012020]

As a user I'd like to add any number of user defined fields to the project.
As a user I'd like to add any number of user defined fields to the issues created inside project.

Fields can be marker as required or optional and system should allow to change if it's mandatory or not on the fly.

Available field types:
* String field - named field which accepts free text defined by the user [done 15022020]
* Number field - named field which accepts number defined by the user. User can define number range, if decimal number are accepted and numbers sign
* Boolean field - named field which allows to set simple boolean values. User can map any text to positive and negative values (yes/no, included/excluded, etc).
  Null value (non defined, N/A) is also available and user can define custom text for it and if it's available for the user. 
* Label field - field for which values can be populated on the fly and in case value exist it'll be proposed by the system [done 15022020]
* Choice field - field along with available choices and if field can be multiple choice or single choice user can also include additional icons associated with the field
* Date time field - field which stores date and time or only date depending on user configuration

As a system administrator I'd like to define fields added by default to every project created. [done 15022020]
As a system administrator I'd like to change fields added to every project created at any time. [done 15022020]

As a project administrator I'd like to define fields added to every issue created. [done 15022020]
As a project administrator I'd like to change set of default fields for issues at any time. [done 15022020]

Field can be modified at any time without affecting existing values [done 15022020]

#### user & user management & permissions [16022020]

As a user I'd like to be able to access it with my credentials.

As system administrator I'd like to define user roles on project basis so I can easily manage users and their permissions in bulk.
  
Each role allows to perform certain actions in the system.
Following actions are defined:
* Creating/Removing/Updating other users
* Updating current user data
* Creating projects
* Updating project (single)
* Creating issues within project
* Viewing issues within project

#### [Future] audit
#### [Future] watching issues
#### [Future] time tracking
#### [Future] workflow which can be defined and field restrictions can be applied on each step + predefined workflows
#### [Future] comments
#### [Future] rich text in text fields (comments, descriptions etc)

#### More TBD and documented here

## Building

* Requires java 11

`./mvnw clean package`

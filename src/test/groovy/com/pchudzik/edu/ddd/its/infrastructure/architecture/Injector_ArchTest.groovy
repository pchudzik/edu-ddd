package com.pchudzik.edu.ddd.its.infrastructure.architecture

import com.google.inject.Module

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

class Injector_ArchTest extends ArchSpecification {
    def "all guice modules must start with _ and end with ContextModule suffix"() {
        expect:
        classes()
                .that().areAssignableTo(Module)
                .should().haveSimpleNameStartingWith('_')
                .andShould().haveSimpleNameEndingWith("ContextModule")
                .check(package_)
    }
}

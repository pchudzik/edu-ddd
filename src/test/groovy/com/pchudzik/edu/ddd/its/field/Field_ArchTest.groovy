package com.pchudzik.edu.ddd.its.field

import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.infrastructure.architecture.ArchSpecification

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

class Field_ArchTest extends ArchSpecification {
    def "fields can be access only from project"() {
        expect:
        classes()
                .that().resideInAPackage("com.pchudzik.edu.ddd.its.field")
                .should().onlyBeAccessed()
                .byClassesThat()
                .resideInAnyPackage(
                        "com.pchudzik.edu.ddd.its.field..",
                        "com.pchudzik.edu.ddd.its.project",
                        "com.pchudzik.edu.ddd.its.issue")
                .orShould().onlyBeAccessed()
                .byClassesThat()
                .belongToAnyOf(InjectorFactory)
                .check(package_)
    }
}

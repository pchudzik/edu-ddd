package com.pchudzik.edu.ddd.its.infrastructure.architecture

import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import spock.lang.Specification

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

class TestNaming_ArchTest extends ArchSpecification {
    def "acceptance tests ends with ATest and reside in proper package"() {
        expect:
        classes()
                .that().areAssignableTo(DbSpecification).and().resideInAPackage("com.pchudzik.edu.ddd.its.test.acceptance")
                .should()
                .haveNameMatching(".*_ATest")
                .check(package_)
    }

    def "architecture tests ends with ArchTest suffix"() {
        expect:
        classes()
                .that().areAssignableTo(ArchSpecification)
                .should()
                .haveNameMatching(".*_ArchTest").orShould().be(ArchSpecification)
                .check(package_)
    }

    def "all tests end with Test Suffix"() {
        expect:
        classes()
                .that().areAssignableTo(Specification)
                .should()
                .haveNameMatching(".*Test")
                .orShould().be(ArchSpecification).orShould().be(DbSpecification)
                .check(package_)
    }
}

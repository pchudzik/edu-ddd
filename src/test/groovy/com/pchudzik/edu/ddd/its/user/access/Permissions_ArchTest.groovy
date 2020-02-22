package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.infrastructure.architecture.ArchSpecification
import com.pchudzik.edu.ddd.its.user.access.Permission
import com.pchudzik.edu.ddd.its.user.access.PermissionFactory
import com.pchudzik.edu.ddd.its.user.access.UserPermissions

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

class Permissions_ArchTest extends ArchSpecification {
    def "permissions can be created only via factory"() {
        expect:
        classes()
                .that().areAssignableFrom(Permission)
                .should().onlyBeAccessed()
                .byClassesThat().belongToAnyOf(PermissionFactory, UserPermissions)
                .check(package_)
    }
}

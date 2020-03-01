package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.infrastructure.architecture.ArchSpecification
import com.pchudzik.edu.ddd.its.user.access.Permission
import com.pchudzik.edu.ddd.its.user.access.PermissionFactory

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

class Permissions_ArchTest extends ArchSpecification {
    def "permissions can be created only via factory"() {
        expect:
        classes()
                .that().areAssignableTo(Permission)
                .should().onlyBeAccessed()
                .byClassesThat().belongToAnyOf(PermissionFactory, ApplicablePermissions)
                .orShould().beAssignableTo(Permission)
                .check(package_)
    }

    def "applicable permissions are created only in RolePermissions or UserPermissions classes"() {
        expect:
        classes()
                .that().areAssignableTo(ApplicablePermissions)
                .should().onlyBeAccessed()
                .byClassesThat().belongToAnyOf(RolePermissions, UserPermissions, ApplicablePermissions)
                .check(package_)
    }
}

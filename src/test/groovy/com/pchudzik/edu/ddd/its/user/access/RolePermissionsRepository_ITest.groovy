package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.TestInjectorFactory
import com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.access.EmptyPrincipal
import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.test.acceptance.Fixtures
import com.pchudzik.edu.ddd.its.user.Users
import com.pchudzik.edu.ddd.its.user.Users.UserCreationCommand
import org.jdbi.v3.core.Jdbi
import spock.lang.Unroll

class RolePermissionsRepository_ITest extends DbSpecification {
    def jdbi = TestInjectorFactory.realSecurityInjector().getInstance(Jdbi)
    def repository = TestInjectorFactory.realSecurityInjector().getInstance(RolePermissionsRepository)
    def users = TestInjectorFactory.injector().getInstance(Users)

    private ProjectId projectId

    def setup() {
        projectId = Fixtures.projectFixture().creator()
                .id("ABCD")
                .create()
    }

    @Unroll
    def "permission of type #permissionType is persisted"() {
        given:
        def role = new RolePermissions(
                "test",
                [PermissionFactory.createPermission(permissionType, projectId)])

        when:
        repository.save(role.getSnapshot())

        then:
        def saved = jdbi.withHandle({
            it
                    .select("select * from permissions where owner_id = :owner")
                    .bind("owner", role.getSnapshot().id.value)
                    .mapToMap()
                    .one()
        })

        saved["id"] != null
        saved["owner_id"] == role.id.value
        saved["type"] == permissionType.name()
        if (permissionType.isProjectLevel()) {
            assert saved["project"] == projectId.value
        }

        where:
        permissionType << PermissionType.values()
    }

    @Unroll
    def "permission of type #permissionType is read"() {
        given:
        def role = new RolePermissions(
                "test",
                [PermissionFactory.createPermission(permissionType, projectId)])
        repository.save(role.getSnapshot())

        when:
        def found = repository.findOne(role.id).getSnapshot()

        then:
        found.id == role.id
        found.name == "test"
        found.permissions.size() == 1
        found.permissions[0].permissionType == permissionType
        if (permissionType.projectLevel) {
            assert found.permissions[0].projectId.get() == projectId
        } else {
            assert !found.permissions[0].projectId.isPresent()
        }

        where:
        permissionType << PermissionType.values()
    }

    def "permissions name is updated on update"() {
        given:
        def role = new RolePermissions("test", [])
        repository.save(role.getSnapshot())

        and:
        role.updateName("new name")

        when:
        repository.save(role.snapshot)

        then:
        def found = repository.findOne(role.id).getSnapshot()
        found.name == "new name"
    }

    def "permissions are added when update"() {
        given:
        def role = new RolePermissions("test", [])
        repository.save(role.getSnapshot())

        and:
        role.updatePermissions(PermissionType.values().collect { PermissionFactory.createPermission(it, projectId) })

        when:
        repository.save(role.snapshot)

        then:
        def found = repository.findOne(role.id).getSnapshot()
        found.permissions.size() == PermissionType.values().size()
    }

    def "all permissions are removed when update"() {
        given:
        def role = new RolePermissions(
                "test",
                PermissionType.values().collect { PermissionFactory.createPermission(it, projectId) })
        repository.save(role.getSnapshot())

        and:
        role.updatePermissions([])

        when:
        repository.save(role.snapshot)

        then:
        def found = repository.findOne(role.id).getSnapshot()
        found.permissions.isEmpty()
    }

    def "assigns role to user"() {
        given:
        def userId = users.createUser(UserCreationCommand.builder()
                .principal(new EmptyPrincipal())
                .login("login")
                .build())
        def role = new RolePermissions("test", [])
        repository.save(role.getSnapshot())

        when:
        repository.assignRoleToUser(userId, role.id)

        then:
        def found = jdbi.withHandle({ h ->
            h
                    .select("select * from user_roles")
                    .mapToMap()
                    .one()
        })
        found["role_id"] == role.id.value
        found["user_id"] == userId.value
    }
}

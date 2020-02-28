package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.TestInjectorFactory
import com.pchudzik.edu.ddd.its.project.ProjectId
import org.jdbi.v3.core.Jdbi
import spock.lang.PendingFeature
import spock.lang.Unroll

class RolePermissionsRepository_ITest extends DbSpecification {
    private static final projectId = new ProjectId("ABCD")
    def jdbi = TestInjectorFactory.realSecurityInjector().getInstance(Jdbi)
    def repository = TestInjectorFactory.realSecurityInjector().getInstance(RolePermissionsRepository)

    @PendingFeature
    @Unroll
    def "permission of type #permissionType is persisted"() {
        given:
        def role = new RolePermissions("test", [PermissionFactory.createPermission(permissionType, projectId)])

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
}

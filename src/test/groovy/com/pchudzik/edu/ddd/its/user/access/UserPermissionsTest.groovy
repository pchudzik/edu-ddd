package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.user.UserId
import spock.lang.Specification
import spock.lang.Unroll

class UserPermissionsTest extends Specification {
    def "user creator can modify and create users"() {
        given:
        def user = AccessFixtures.user(PermissionFactory.userManager())

        expect:
        user.canManageUsers()
    }

    def "update user data"() {
        given:
        def regularUser = AccessFixtures.user()
        def adminUser = AccessFixtures.user(PermissionFactory.userManager())

        expect:
        regularUser.canManageUser(regularUser.userId)
        !regularUser.canManageUser(adminUser.userId)

        and:
        adminUser.canManageUser(regularUser.userId)
        adminUser.canManageUser(adminUser.userId)
    }

    @Unroll
    def "administrator can do all"() {
        given:
        def projectId = new ProjectId("ABC")
        def user = AccessFixtures.user(PermissionFactory.administrator())

        expect:
        checkAction(user, projectId) == true

        where:
        checkAction << [
                { UserPermissions u, ProjectId p -> u.canAccessIssue(p) },
                { UserPermissions u, ProjectId p -> u.canManageUsers() },
                { UserPermissions u, ProjectId p -> u.canCreateIssue(p) },
                { UserPermissions u, ProjectId p -> u.canCreateProject() },
                { UserPermissions u, ProjectId p -> u.canManageUser(u.getUserId()) },
                { UserPermissions u, ProjectId p -> u.canManageUser(new UserId()) },
                { UserPermissions u, ProjectId p -> u.canManageProject(p) }
        ]
    }

}

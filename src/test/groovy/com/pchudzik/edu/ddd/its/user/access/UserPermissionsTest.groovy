package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.user.UserId
import com.pchudzik.edu.ddd.its.user.access.Permission
import com.pchudzik.edu.ddd.its.user.access.PermissionFactory
import com.pchudzik.edu.ddd.its.user.access.UserPermissions
import spock.lang.Specification
import spock.lang.Unroll

class UserPermissionsTest extends Specification {
    def "user creator can modify and create users"() {
        given:
        def user = user(PermissionFactory.userCreator())

        expect:
        user.canAddUser()
    }

    def "user with permission to current project can manage it"() {
        given:
        def projectToManage = new ProjectId("ABC")
        def otherProject = new ProjectId("ZXC")
        def user = user(PermissionFactory.singleProjectManager(projectToManage))

        expect:
        user.canManageProject(projectToManage)
        !user.canManageProject(otherProject)
    }

    def "user with permission to creating issues in current project can create them"() {
        given:
        def projectWithAccess = new ProjectId("ABC")
        def otherProject = new ProjectId("ZXC")
        def user = user(PermissionFactory.issueCreatorWithinProject(projectWithAccess))

        expect:
        user.canCreateIssue(projectWithAccess)
        !user.canCreateIssue(otherProject)
    }

    def "user with project creator role can create projects"() {
        given:
        def user = user(PermissionFactory.newProjectCreator())

        expect:
        user.canCreateProject()
    }

    def "update user data"() {
        given:
        def regularUserWithoutPermissions = user()
        def regularUser = user(PermissionFactory.userDataManager())
        def adminUser = user(PermissionFactory.userCreator())

        expect:
        regularUserWithoutPermissions.canEditUser(regularUserWithoutPermissions.userId)
        !regularUserWithoutPermissions.canEditUser(adminUser.userId)

        and:
        !regularUser.canEditUser(adminUser.userId)
        regularUser.canEditUser(regularUser.userId)

        and:
        adminUser.canEditUser(regularUser.userId)
        adminUser.canEditUser(adminUser.userId)
    }

    def "user can view issues within project"() {
        given:
        def projectId = new ProjectId("ABC")
        def otherProjectId = new ProjectId("OTHER")
        def user = user(PermissionFactory.accessIssue(projectId))

        expect:
        user.canAccessIssue(projectId)
        !user.canAccessIssue(otherProjectId)
    }

    @Unroll
    def "administrator can do all"() {
        given:
        def projectId = new ProjectId("ABC")
        def user = user(PermissionFactory.administrator())

        expect:
        checkAction(user, projectId) == true

        where:
        checkAction << [
                { UserPermissions u, ProjectId p -> u.canAccessIssue(p) },
                { UserPermissions u, ProjectId p -> u.canAddUser() },
                { UserPermissions u, ProjectId p -> u.canCreateIssue(p) },
                { UserPermissions u, ProjectId p -> u.canCreateProject() },
                { UserPermissions u, ProjectId p -> u.canEditUser(u.getUserId()) },
                { UserPermissions u, ProjectId p -> u.canEditUser(new UserId()) },
                { UserPermissions u, ProjectId p -> u.canManageProject(p) }
        ]
    }

    private UserPermissions user(Permission... permissions) {
        new UserPermissions(new UserId(), Arrays.asList(permissions))
    }
}

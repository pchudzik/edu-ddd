package com.pchudzik.edu.ddd.its.user

import com.pchudzik.edu.ddd.its.project.ProjectId
import spock.lang.Specification

class UserPermissionsTest extends Specification {
    def "only administrator can modify users"() {
        given:
        def user = user(new PermissionUserCreator())

        expect:
        user.canAddUser()
    }

    def "user with permission to current project can manage it"() {
        given:
        def projectToManage = new ProjectId("ABC")
        def otherProject = new ProjectId("ZXC")
        def user = user(new PermissionSingleProjectManager(projectToManage))

        expect:
        user.canManageProject(projectToManage)
        !user.canManageProject(otherProject)
    }

    def "user with permission to creating issues in current project can create them"() {
        given:
        def projectToManage = new ProjectId("ABC")
        def otherProject = new ProjectId("ZXC")
        def user = user(new PermissionIssueCreatorWithinProject(projectToManage))

        expect:
        user.canCreateIssue(projectToManage)
        !user.canCreateIssue(otherProject)
    }

    def "user with project creator role can create projects"() {
        given:
        def user = user(new PermissionNewProjectCreator())

        expect:
        user.canCreateProject()
    }

    def "update user data"() {
        given:
        def regularUser = user(new PermissionUserDataManager())
        def adminUser = user(new PermissionUserCreator())

        expect:
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
        def user = this.user(new PermissionAccessIssue(projectId))

        expect:
        user.canAccessIssue(projectId)
        !user.canAccessIssue(otherProjectId)
    }

    private UserPermissions user(Permission... permissions) {
        new UserPermissions(new UserId(), Arrays.asList(permissions))
    }
}

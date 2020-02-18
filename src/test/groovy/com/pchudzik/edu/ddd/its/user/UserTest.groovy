package com.pchudzik.edu.ddd.its.user

import com.pchudzik.edu.ddd.its.project.ProjectId
import spock.lang.Specification

class UserTest extends Specification {
    def "only administrator can modify users"() {
        given:
        def user = user(new PermissionUserManager())

        expect:
        user.canAddUser()
    }

    def "user with permission to current project can manage it"() {
        given:
        def projectToManage = new ProjectId("ABC")
        def otherProject = new ProjectId("ZXC")
        def user = user(new PermissionProjectManager(projectToManage))

        expect:
        user.canManageProject(projectToManage)
        !user.canManageProject(otherProject)
    }

    def "user with permission to creating issues in current project can create them"() {
        given:
        def projectToManage = new ProjectId("ABC")
        def otherProject = new ProjectId("ZXC")
        def user = user(new PermissionIssueCreator(projectToManage))

        expect:
        user.canCreateIssue(projectToManage)
        !user.canCreateIssue(otherProject)
    }

    private UserPermissions user(Permission ... permissions) {
        new UserPermissions(new UserId(), Arrays.asList(permissions))
    }
}

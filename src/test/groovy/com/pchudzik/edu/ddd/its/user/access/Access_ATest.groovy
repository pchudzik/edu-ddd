package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.user.UserId
import com.pchudzik.edu.ddd.its.user.access.Access.SecuredAction
import com.pchudzik.edu.ddd.its.user.access.Access.SecuredOperation
import spock.lang.Specification

import static com.pchudzik.edu.ddd.its.user.access.PermissionFactory.accessIssue
import static com.pchudzik.edu.ddd.its.user.access.PermissionFactory.issueCreatorWithinProject

class Access_ATest extends Specification {
    private static final project = new ProjectId("ABC")
    def repo = new StubbedUserPermissionsRepository()
    def access = new AccessImpl(repo)

    def "user with permission to creating issues in current project can create them"() {
        given:
        def action = Mock(SecuredAction)
        def principal = repo.addUser(AccessFixtures.user(issueCreatorWithinProject(project)))

        when:
        access.ifCanCreateIssue(principal, project, action)

        then:
        1 * action.apply()
    }

    def "user without permission to creating issues in current project can create them"() {
        given:
        def action = Mock(SecuredAction)
        def principal = repo.addUser(AccessFixtures.user())

        when:
        access.ifCanCreateIssue(principal, project, action)

        then:
        thrown(AccessException)
        0 * action.apply()
    }

    def "user with permission to creating issues in current project can update them"() {
        given:
        def operation = Mock(SecuredOperation)
        def principal = repo.addUser(AccessFixtures.user(issueCreatorWithinProject(project)))

        when:
        access.ifCanUpdateIssue(principal, project, operation)

        then:
        1 * operation.execute()
    }

    def "user without permission to creating issues in current project can not update them"() {
        given:
        def operation = Mock(SecuredOperation)
        def principal = repo.addUser(AccessFixtures.user())

        when:
        access.ifCanUpdateIssue(principal, project, operation)

        then:
        thrown(AccessException)
        0 * operation.execute()
    }

    def "user with permission can access issue"() {
        given:
        def action = Mock(SecuredAction)
        def principal = repo.addUser(AccessFixtures.user(accessIssue(project)))

        when:
        access.ifCanViewIssue(principal, project, action)

        then:
        1 * action.apply()
    }

    def "user without permission cannot access issue"() {
        given:
        def action = Mock(SecuredAction)
        def principal = repo.addUser(AccessFixtures.user())

        when:
        access.ifCanViewIssue(principal, project, action)

        then:
        thrown(AccessException)
        0 * action.apply()
    }

    def "user with permission can create projects"() {
        given:
        def action = Mock(SecuredAction)
        def principal = repo.addUser(AccessFixtures.user(PermissionFactory.newProjectCreator()))

        when:
        access.ifCanCreateProject(principal, action)

        then:
        1 * action.apply()
    }

    def "user without permission cannot create projects"() {
        given:
        def action = Mock(SecuredAction)
        def principal = repo.addUser(AccessFixtures.user())

        when:
        access.ifCanCreateProject(principal, action)

        then:
        thrown(AccessException)
        0 * action.apply()
    }

    def "user with permission to current project can manage it"() {
        given:
        def action = Mock(SecuredOperation)
        def projectManager = repo.addUser(AccessFixtures.user(permission))

        when:
        access.ifCanUpdateProject(projectManager, project, action)

        then:
        1 * action.execute()

        where:
        permission << [
                PermissionFactory.singleProjectManager(project),
                PermissionFactory.newProjectCreator()
        ]
    }

    def "user with permission to current project cannot manage it"() {
        given:
        def action = Mock(SecuredOperation)
        def principal = repo.addUser(AccessFixtures.user())

        when:
        access.ifCanUpdateProject(principal, project, action)

        then:
        thrown AccessException
        0 * action.execute()
    }

    def "user with permission can create and manage roles"() {
        given:
        def action = Mock(SecuredAction)
        def user = repo.addUser(AccessFixtures.user(PermissionFactory.rolesManager()))

        when:
        access.ifCanManageRoles(user, action)

        then:
        1 * action.apply()
    }

    def "user without permission cannot create nor manage roles"() {
        given:
        def action = Mock(SecuredAction)
        def principal = repo.addUser(AccessFixtures.user())

        when:
        access.ifCanManageRoles(principal, action)

        then:
        thrown AccessException
        0 * action.apply()
    }

    def "user with permission to current project can access it"() {
        given:
        def principal = repo.addUser(AccessFixtures.user(PermissionFactory.accessProject(project)))

        expect:
        access.canViewProject(principal).test(project)
    }

    def "user with permission to current project cannot access it"() {
        given:
        def principal = repo.addUser(AccessFixtures.user())

        expect:
        !access.canViewProject(principal).test(project)
    }

    private static class StubbedUserPermissionsRepository implements PermissionsRepository {
        private Map<UserId, UserPermissions> repo = [:]

        Principal addUser(UserPermissions permissions) {
            repo[permissions.userId] = permissions
            return Principal.of(permissions.userId)
        }

        @Override
        Optional<UserPermissions> findOne(UserId userId) {
            return Optional.ofNullable(repo[userId])
        }
    }
}

package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.user.UserId
import com.pchudzik.edu.ddd.its.user.access.Access.Principal
import com.pchudzik.edu.ddd.its.user.access.Access.SecuredAction
import com.pchudzik.edu.ddd.its.user.access.Access.SecuredOperation
import spock.lang.Specification

import static com.pchudzik.edu.ddd.its.user.access.PermissionFactory.issueCreatorWithinProject

class Access_ATest extends Specification {
    def repo = new StubbedUserPermissionsRepository()
    def access = new AccessImpl(repo)

    def "user with permission to creating issues in current project can create them"() {
        given:
        def action = Mock(SecuredAction)
        def project = new ProjectId("ABC")
        def principal = repo.addUser(AccessFixtures.user(issueCreatorWithinProject(project)))

        when:
        access.ifCanCreateIssue(principal, project, action)

        then:
        1 * action.apply()
    }

    def "user without permission to creating issues in current project can create them"() {
        given:
        def action = Mock(SecuredAction)
        def project = new ProjectId("ABC")
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
        def project = new ProjectId("ABC")
        def principal = repo.addUser(AccessFixtures.user(issueCreatorWithinProject(project)))

        when:
        access.ifCanUpdateIssue(principal, project, operation)

        then:
        1 * operation.execute()
    }

    def "user without permission to creating issues in current project can not update them"() {
        given:
        def operation = Mock(SecuredOperation)
        def project = new ProjectId("ABC")
        def principal = repo.addUser(AccessFixtures.user())

        when:
        access.ifCanUpdateIssue(principal, project, operation)

        then:
        thrown(AccessException)
        0 * operation.execute()
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

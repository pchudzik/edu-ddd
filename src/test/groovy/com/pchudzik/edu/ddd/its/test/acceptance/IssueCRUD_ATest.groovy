package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.issue.IssueFacade
import com.pchudzik.edu.ddd.its.issue.read.IssueReadFacade

class IssueCRUD_ATest extends DbSpecification {
    def injector = InjectorFactory.injector()

    def issueFacade = injector.getInstance(IssueFacade)
    def issueReadFacade = injector.getInstance(IssueReadFacade)

    def "new issue is created"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()

        when:
        def issueId = issueFacade.createIssue(IssueFacade.IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .build())

        then:
        def issue = issueReadFacade.findIssue(issueId)
        issue.id == issueId
        issue.title == "some issue"
    }
}

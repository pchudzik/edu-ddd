package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.issue.IssueFacade
import com.pchudzik.edu.ddd.its.issue.read.IssueReadFacade
import com.pchudzik.edu.ddd.its.project.ProjectFacade
import com.pchudzik.edu.ddd.its.project.ProjectId

class IssueCRUD_ATest extends DbSpecification {
    def injector = InjectorFactory.injector()

    def projectFacade = injector.getInstance(ProjectFacade)
    def issueFacade = injector.getInstance(IssueFacade)
    def issueReadFacade = injector.getInstance(IssueReadFacade)

    def "new issue is created"() {
        given:
        def projectId = projectFacade.createNewProject(ProjectFacade.ProjectCreationCommand.builder()
                .id(new ProjectId("ABCD"))
                .name("Some Project")
                .description("Some description")
                .build())

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

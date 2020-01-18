package com.pchudzik.edu.ddd.its.test.acceptance;

import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.issue.id.IssueIdGenerator
import com.pchudzik.edu.ddd.its.project.ProjectFacade
import com.pchudzik.edu.ddd.its.project.ProjectId
import spock.lang.Specification;

class IssueId_ATest extends Specification {
    private def injector = InjectorFactory.injector()
    private def projectFacade = injector.getInstance(ProjectFacade)
    private def issueIdGenerator = injector.getInstance(IssueIdGenerator)

    def "can create issue ids when new project is created"() {
        given:
        def projectId = projectFacade.createNewProject(ProjectFacade.ProjectCreationCommand.builder()
                .id(new ProjectId("ABC"))
                .name("some project")
                .description("test project")
                .build())

        when:
        def id1 = issueIdGenerator.next(projectId)
        def id2 = issueIdGenerator.next(projectId)

        then:
        id1.toString() == "ABC-1"
        id2.toString() == "ABC-2"
    }
}

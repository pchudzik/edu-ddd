package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldCreationFacade
import com.pchudzik.edu.ddd.its.field.FieldId
import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.issue.IssueFacade
import com.pchudzik.edu.ddd.its.issue.id.IssueId
import com.pchudzik.edu.ddd.its.project.ProjectFacade
import com.pchudzik.edu.ddd.its.project.ProjectId

import java.util.concurrent.atomic.AtomicInteger

class Fixtures {
    private static ProjectFixture projectFixture
    private static IssueFixture issueFixture
    private static FieldFixture fieldFixture

    static ProjectFixture projectFixture() {
        if (projectFixture == null) {
            def injector = InjectorFactory.injector()
            projectFixture = new ProjectFixture(projectFacade: injector.getInstance(ProjectFacade))
        }

        return projectFixture
    }

    static FieldFixture fieldFixture() {
        if (fieldFixture == null) {
            def injector = InjectorFactory.injector()
            fieldFixture = new FieldFixture(fieldCreationFacade: injector.getInstance(FieldCreationFacade))
        }
        return fieldFixture
    }

    static class FieldFixture {
        private FieldCreationFacade fieldCreationFacade

        FieldId createNewStringField() {
            return fieldCreationFacade.createStringField(FieldCreationFacade.StringFieldCreationCommand.builder()
                    .fieldName("string field")
                    .required(true)
                    .minLength(2)
                    .maxLength(20)
                    .build())

        }
    }

    static IssueFixture issueFixture() {
        if (issueFixture == null) {
            def injector = InjectorFactory.injector()
            issueFixture = new IssueFixture(issueFacade: injector.getInstance(IssueFacade))
        }
        return issueFixture
    }

    static class IssueFixture {
        private IssueFacade issueFacade

        IssueId createNewIssue(ProjectId projectId) {
            return issueFacade.createIssue(IssueFacade.IssueCreationCommand.builder()
                    .projectId(projectId)
                    .title("some issue")
                    .build())

        }
    }

    static class ProjectFixture {
        private final AtomicInteger index = new AtomicInteger(0)
        private ProjectFacade projectFacade

        ProjectId createNewProject(String name) {
            projectFacade.createNewProject(ProjectFacade.ProjectCreationCommand.builder()
                    .id(new ProjectId(name))
                    .name("some project")
                    .description("test project")
                    .build())

        }

        ProjectId createNewProject() {
            return createNewProject("ABC" + index.incrementAndGet())
        }
    }
}

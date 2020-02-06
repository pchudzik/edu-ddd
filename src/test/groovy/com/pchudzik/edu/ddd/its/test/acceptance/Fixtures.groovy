package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldCreation
import com.pchudzik.edu.ddd.its.field.FieldId
import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.issue.IssueCreation
import com.pchudzik.edu.ddd.its.issue.id.IssueId
import com.pchudzik.edu.ddd.its.project.ProjectCreation
import com.pchudzik.edu.ddd.its.project.ProjectId

import java.util.concurrent.atomic.AtomicInteger

class Fixtures {
    private static ProjectFixture projectFixture
    private static IssueFixture issueFixture
    private static FieldFixture fieldFixture

    static ProjectFixture projectFixture() {
        if (projectFixture == null) {
            def injector = InjectorFactory.injector()
            projectFixture = new ProjectFixture(projectFacade: injector.getInstance(ProjectCreation))
        }

        return projectFixture
    }

    static FieldFixture fieldFixture() {
        if (fieldFixture == null) {
            def injector = InjectorFactory.injector()
            fieldFixture = new FieldFixture(fieldCreationFacade: injector.getInstance(FieldCreation))
        }
        return fieldFixture
    }

    static class FieldFixture {
        private FieldCreation fieldCreationFacade

        FieldId createNewStringField() {
            return fieldCreationFacade.createStringField(FieldCreation.StringFieldCreationCommand.builder()
                    .fieldName("string field")
                    .required(true)
                    .build())
        }

        FieldId createNewLabelField() {
            return fieldCreationFacade.createLabelField(FieldCreation.LabelFieldCreationCommand.builder()
                    .fieldName("label field")
                    .required(true)
                    .build())
        }
    }

    static IssueFixture issueFixture() {
        if (issueFixture == null) {
            def injector = InjectorFactory.injector()
            issueFixture = new IssueFixture(issueFacade: injector.getInstance(IssueCreation))
        }
        return issueFixture
    }

    static class IssueFixture {
        private IssueCreation issueFacade

        IssueId createNewIssue(ProjectId projectId) {
            return issueFacade.createIssue(IssueCreation.IssueCreationCommand.builder()
                    .projectId(projectId)
                    .title("some issue")
                    .build())

        }
    }

    static class ProjectFixture {
        private final AtomicInteger index = new AtomicInteger(0)
        private ProjectCreation projectFacade

        ProjectId createNewProject(String name) {
            projectFacade.createNewProject(ProjectCreation.ProjectCreationCommand.builder()
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

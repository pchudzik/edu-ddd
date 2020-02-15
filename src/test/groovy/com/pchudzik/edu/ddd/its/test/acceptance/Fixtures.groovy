package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldCreation
import com.pchudzik.edu.ddd.its.field.FieldCreation.StringFieldCreationCommand
import com.pchudzik.edu.ddd.its.field.FieldId
import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitions
import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.issue.IssueCreation
import com.pchudzik.edu.ddd.its.issue.IssueCreation.FieldToIssueAssignmentCommand
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
            projectFixture = new ProjectFixture(
                    projectFacade: injector.getInstance(ProjectCreation),
                    fieldDefinitions: injector.getInstance(FieldDefinitions))
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
            return fieldCreationFacade.createStringField(StringFieldCreationCommand.builder()
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
            issueFixture = new IssueFixture(issueCreation: injector.getInstance(IssueCreation))
        }
        return issueFixture
    }

    static class IssueFixture {
        private IssueCreation issueCreation

        IssueId createNewIssue(ProjectId projectId, FieldToIssueAssignmentCommand ... assignments) {
            def cmd = IssueCreation.IssueCreationCommand.builder()
                    .projectId(projectId)
                    .title("some issue")

            if(assignments) {
                cmd.fieldAssignments(Arrays.asList(assignments))
            }

            return issueCreation.createIssue(cmd.build())
        }
    }

    static class ProjectFixture {
        private final AtomicInteger index = new AtomicInteger(0)
        private ProjectCreation projectFacade
        private FieldDefinitions fieldDefinitions

        ProjectId createNewProject(String name) {
            projectFacade.createNewProject(ProjectCreation.ProjectCreationCommand.builder()
                    .id(new ProjectId(name))
                    .name("some project")
                    .description("test project")
                    .build())

        }

        ProjectId createNewProject(FieldId... fields) {
            def projectId = createNewProject("ABC" + index.incrementAndGet())
            if (fields) {
                fieldDefinitions.assignDefaultFields(projectId, Arrays.asList(fields))
            }
            return projectId
        }
    }
}

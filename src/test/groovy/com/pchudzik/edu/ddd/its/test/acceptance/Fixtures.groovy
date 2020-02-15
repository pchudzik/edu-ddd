package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldCreation
import com.pchudzik.edu.ddd.its.field.FieldCreation.LabelFieldCreationCommand
import com.pchudzik.edu.ddd.its.field.FieldCreation.StringFieldCreationCommand
import com.pchudzik.edu.ddd.its.field.FieldId
import com.pchudzik.edu.ddd.its.field.FieldValueAssignmentCommand
import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitions
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

        StringFieldCreator stringFieldCreator() {
            return new StringFieldCreator()
        }

        LabelFieldCreator labelFieldCreator() {
            return new LabelFieldCreator()
        }

        FieldId createNewStringField() {
            stringFieldCreator().create()
        }

        FieldId createNewLabelField() {
            return labelFieldCreator().create()
        }

        class StringFieldCreator {
            String name = "string field"
            boolean required = true

            StringFieldCreator name(String name) {
                this.name = name
                return this
            }

            StringFieldCreator required(boolean required) {
                this.required = required
                return this
            }

            FieldId create() {
                return fieldCreationFacade.createStringField(StringFieldCreationCommand.builder()
                        .fieldName(name)
                        .required(required)
                        .build())
            }
        }

        class LabelFieldCreator {
            String name = "label field"
            boolean required = true

            StringFieldCreator name(String name) {
                this.name = name
                return this
            }

            StringFieldCreator required(boolean required) {
                this.required = required
                return this
            }

            FieldId create() {
                return fieldCreationFacade.createLabelField(LabelFieldCreationCommand.builder()
                        .fieldName(name)
                        .required(required)
                        .build())
            }

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

        IssueId createNewIssue(ProjectId projectId, FieldValueAssignmentCommand... assignments) {
            def cmd = IssueCreation.IssueCreationCommand.builder()
                    .projectId(projectId)
                    .title("some issue")

            if (assignments) {
                cmd.fieldAssignments(Arrays.asList(assignments))
            }

            return issueCreation.createIssue(cmd.build())
        }
    }

    static class ProjectFixture {
        private final AtomicInteger index = new AtomicInteger(0)
        private ProjectCreation projectFacade
        private FieldDefinitions fieldDefinitions

        CreationBuilder creator() {
            return new CreationBuilder()
        }

        class CreationBuilder {
            String id = null
            String name = "some project"
            String description = "some project description"
            Collection<FieldId> definitions = []
            Collection<FieldValueAssignmentCommand> assignments = []

            CreationBuilder id(String id) {
                this.id = id
                return this
            }

            CreationBuilder addDefinition(FieldId... definitions) {
                if (definitions) {
                    definitions.each { this.definitions.add(it) }
                }
                return this
            }

            CreationBuilder addAssignment(FieldValueAssignmentCommand... cmds) {
                if (cmds) {
                    cmds.each { assignments.add(it) }
                }
                return this
            }

            ProjectId create() {
                def projectId = projectFacade.createNewProject(ProjectCreation.ProjectCreationCommand.builder()
                        .id(new ProjectId(id ?: ("ABC" + index.incrementAndGet())))
                        .name(name)
                        .description(description)
                        .fieldAssignments(new ArrayList<>(assignments))
                        .build())

                if (definitions) {
                    fieldDefinitions.assignDefaultFields(projectId, new ArrayList<FieldId>(definitions))
                }

                return projectId
            }
        }

        ProjectId createNewProject(FieldId... fields) {
            return creator()
                    .addDefinition(fields)
                    .create()
        }
    }
}

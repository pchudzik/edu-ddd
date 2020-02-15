package com.pchudzik.edu.ddd.its.test.acceptance


import com.pchudzik.edu.ddd.its.field.FieldAssignmentException
import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.field.FieldValueAssignmentCommand
import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitions
import com.pchudzik.edu.ddd.its.field.read.FieldValues
import com.pchudzik.edu.ddd.its.field.read.FieldValues.StringValue
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.project.ProjectCreation
import com.pchudzik.edu.ddd.its.project.ProjectCreation.ProjectCreationCommand
import com.pchudzik.edu.ddd.its.project.ProjectCreation.ProjectUpdateCommand
import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.project.read.ProjectView
import com.pchudzik.edu.ddd.its.project.read.ProjectView.ProjectDto

class ProjectCRUD_ATest extends DbSpecification {
    def projectCreation = injector.getInstance(ProjectCreation)
    def projectView = injector.getInstance(ProjectView)
    def fieldDefinitions = injector.getInstance(FieldDefinitions)
    def fieldValues = injector.getInstance(FieldValues)

    def "new project is created"() {
        given:
        def projectId = new ProjectId("ABCD")

        when:
        projectCreation.createNewProject(ProjectCreation.ProjectCreationCommand.builder()
                .id(projectId)
                .name("Some Project")
                .description("Some description")
                .build())

        then:
        def allProjects = projectView.listProjects()

        and:
        allProjects.size() == 1
        allProjects.contains(ProjectView.ProjectDto.builder()
                .id(projectId)
                .name("Some Project")
                .description("Some description")
                .build())
    }

    def "string field value is assigned to project"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()
        fieldDefinitions.assignDefaultFields([fieldId])

        when:
        def projectId = projectCreation
                .createNewProject(ProjectCreationCommand.builder()
                        .id(new ProjectId("ABC"))
                        .name("project")
                        .description("project")
                        .fieldAssignment(new FieldValueAssignmentCommand(fieldId, "string value", FieldType.STRING_FIELD))
                        .build())

        then:
        def fieldsForIssue = fieldValues.findFieldsAssignedToProject(projectId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].getAssignee(ProjectId) == projectId
        fieldsForIssue[0].fieldType == FieldType.STRING_FIELD
        fieldsForIssue[0].getValue(StringValue.class).value == "string value"
        fieldsForIssue[0].getValue(StringValue).id instanceof UUID
    }

    def "label field value is assigned to project"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()
        fieldDefinitions.assignDefaultFields([fieldId])

        when:
        def projectId = projectCreation.createNewProject(ProjectCreationCommand.builder()
                .id(new ProjectId("ABC"))
                .name("project")
                .description("project")
                .fieldAssignment(new FieldValueAssignmentCommand(fieldId, ["first", "second"], FieldType.LABEL_FIELD))
                .build())

        then:
        def fieldsForIssue = fieldValues.findFieldsAssignedToProject(projectId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].getAssignee(ProjectId) == projectId
        fieldsForIssue[0].fieldType == FieldType.LABEL_FIELD
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.collect { it.value } as Set == ["first", "second"] as Set
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.every { it.id != null && it.id instanceof UUID }
    }

    def "required fields are filled when creating project"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()
        fieldDefinitions.assignDefaultFields([fieldId])

        when:
        def projectId = projectCreation.createNewProject(ProjectCreationCommand.builder()
                .name("name")
                .id(new ProjectId("ABC"))
                .description("project")
                .fieldAssignment(new FieldValueAssignmentCommand(fieldId, "value", FieldType.STRING_FIELD))
                .build())

        then:
        def fields = fieldValues.findFieldsAssignedToProject(projectId)
        fields.size() == 1
        fields[0].getAssignee(ProjectId) == projectId
        fields[0].getValue(StringValue).value == "value"
    }

    def "project creation without required field is not possible"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()
        fieldDefinitions.assignDefaultFields([fieldId])

        when:
        projectCreation.createNewProject(ProjectCreationCommand.builder()
                .name("name")
                .id(new ProjectId("ABC"))
                .description("project")
                .build())

        then:
        thrown(FieldAssignmentException)
    }

    def "project creation with not available field is not possible"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        when:
        projectCreation.createNewProject(ProjectCreationCommand.builder()
                .name("name")
                .id(new ProjectId("ABC"))
                .description("project")
                .fieldAssignment(new FieldValueAssignmentCommand(fieldId, "value", FieldType.STRING_FIELD))
                .build())

        then:
        thrown(FieldAssignmentException)
    }

    def "project fields can be updated"() {
        given:
        def field1Id = Fixtures.fieldFixture().stringFieldCreator().name("field1").create()
        def field2Id = Fixtures.fieldFixture().stringFieldCreator().name("field2").create()
        fieldDefinitions.assignDefaultFields([field1Id, field2Id])

        and:
        def projectId = projectCreation.createNewProject(ProjectCreationCommand.builder()
                .name("name")
                .id(new ProjectId("ABC"))
                .description("project")
                .fieldAssignment(new FieldValueAssignmentCommand(field1Id, "value 1", FieldType.STRING_FIELD))
                .fieldAssignment(new FieldValueAssignmentCommand(field2Id, "value 2", FieldType.STRING_FIELD))
                .build())

        when:
        projectCreation.updateProject(projectId, ProjectUpdateCommand.builder()
                .name("new name")
                .description("new description")
                .fieldAssignment(new FieldValueAssignmentCommand(field1Id, "new value 1", FieldType.STRING_FIELD))
                .build())

        then:
        def allProjects = projectView.listProjects()
        allProjects.size() == 1
        allProjects.contains(ProjectDto.builder()
                .id(projectId)
                .name("new name")
                .description("new description")
                .build())

        and:
        def fields = fieldValues.findFieldsAssignedToProject(projectId)
        fields.size() == 2
        fields.collect { it.getAssignee(ProjectId) } == [projectId, projectId]
        fields.collect { it.getValue(StringValue).value } as Set == ["value 2", "new value 1"] as Set
    }
}

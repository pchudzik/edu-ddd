package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldAssignment
import com.pchudzik.edu.ddd.its.field.FieldAssignment.LabelFieldAssignmentCommand
import com.pchudzik.edu.ddd.its.field.FieldAssignment.StringFieldAssignmentCommand
import com.pchudzik.edu.ddd.its.field.FieldCreation
import com.pchudzik.edu.ddd.its.field.FieldCreation.LabelFieldConfigurationUpdateCommand
import com.pchudzik.edu.ddd.its.field.FieldCreation.StringFieldConfigurationUpdateCommand
import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.field.LabelValues
import com.pchudzik.edu.ddd.its.field.LabelValues.LabelValue
import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitions
import com.pchudzik.edu.ddd.its.field.read.FieldValues
import com.pchudzik.edu.ddd.its.field.read.FieldValues.FieldValueDto
import com.pchudzik.edu.ddd.its.field.read.FieldValues.IssueFieldValueDto
import com.pchudzik.edu.ddd.its.field.read.FieldValues.StringValue
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.issue.IssueCreation
import com.pchudzik.edu.ddd.its.issue.IssueCreation.FieldToIssueAssignmentCommand
import com.pchudzik.edu.ddd.its.issue.id.IssueId
import com.pchudzik.edu.ddd.its.project.ProjectId

class FieldValues_ATest extends DbSpecification {
    def fieldAssignmentFacade = injector.getInstance(FieldAssignment)
    def fieldCreation = injector.getInstance(FieldCreation)
    def fieldValuesFacade = injector.getInstance(FieldValues)
    def issueCreation = injector.getInstance(IssueCreation)
    def fieldDefinitions = injector.getInstance(FieldDefinitions)

    def "string field value is assigned to issue"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        when:
        def issueId = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, "ala ma kota", FieldType.STRING_FIELD))

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToIssue(issueId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].getAssignee(IssueId) == issueId
        fieldsForIssue[0].fieldType == FieldType.STRING_FIELD
        fieldsForIssue[0].getValue(StringValue).value == "ala ma kota"
        fieldsForIssue[0].getValue(StringValue).id instanceof UUID
    }

    def "string field value is assigned to project"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        when:
        fieldAssignmentFacade.assignToField(
                projectId,
                [new StringFieldAssignmentCommand(fieldId, "ala ma kota")])

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToProject(projectId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].getAssignee(ProjectId) == projectId
        fieldsForIssue[0].fieldType == FieldType.STRING_FIELD
        fieldsForIssue[0].getValue(StringValue.class).value == "ala ma kota"
        fieldsForIssue[0].getValue(StringValue).id instanceof UUID
    }

    def "assigning new value of string field removes old value"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        and:
        def issueId = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, "old value", FieldType.STRING_FIELD))

        when:
        fieldAssignmentFacade.assignToField(
                issueId,
                [new StringFieldAssignmentCommand(fieldId, "updated value")])

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToIssue(issueId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].getAssignee(IssueId) == issueId
        fieldsForIssue[0].fieldType == FieldType.STRING_FIELD
        fieldsForIssue[0].getValue(StringValue).value == "updated value"
        fieldsForIssue[0].getValue(StringValue).id instanceof UUID
    }

    def "string field value is found when field is updated"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        and:
        def issue1 = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, "a", FieldType.STRING_FIELD))
        and:
        fieldId = fieldCreation.updateStringField(fieldId, StringFieldConfigurationUpdateCommand.builder()
                .minLength(4)
                .maxLength(100)
                .build())

        when:
        def issue2 = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, "second value", FieldType.STRING_FIELD))

        then:
        extractAssignedStringValues(fieldValuesFacade.findFieldsAssignedToIssue(issue1)) == ["a"]
        extractAssignedStringValues(fieldValuesFacade.findFieldsAssignedToIssue(issue2)) == ["second value"]
    }

    def "string field value is updated when field is updated"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        and:
        def issue1 = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, "a", FieldType.STRING_FIELD))

        and:
        fieldId = fieldCreation.updateStringField(fieldId, StringFieldConfigurationUpdateCommand.builder()
                .minLength(4)
                .maxLength(100)
                .build())

        when:
        fieldAssignmentFacade.assignToField(
                issue1,
                [new StringFieldAssignmentCommand(fieldId, "updated value")])

        then:
        extractAssignedStringValues(fieldValuesFacade.findFieldsAssignedToIssue(issue1)) == ["updated value"]
    }

    def "no longer used string field definition is removed when field is not used"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)


        and:
        def issue1 = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, "a", FieldType.STRING_FIELD))

        and:
        fieldId = fieldCreation.updateStringField(fieldId, StringFieldConfigurationUpdateCommand.builder()
                .minLength(4)
                .maxLength(100)
                .build())

        when:
        fieldAssignmentFacade.assignToField(
                issue1,
                [new StringFieldAssignmentCommand(fieldId, "updated value")])

        then:
        FieldLookup.findNumberOfFieldsInAnyVersion(fieldId) == 1
    }


    def "label field value is assigned to issue"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        when:
        def issueId = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, ["first", "second"], FieldType.LABEL_FIELD))

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToIssue(issueId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].getAssignee(IssueId) == issueId
        fieldsForIssue[0].fieldType == FieldType.LABEL_FIELD
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.collect { it.value } as Set == ["first", "second"] as Set
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.every { it.id != null && it.id instanceof UUID }
    }

    def "label field value is assigned to project"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()

        and:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()

        when:
        fieldAssignmentFacade.assignToField(
                projectId,
                [new LabelFieldAssignmentCommand(
                        fieldId,
                        LabelValues.of([
                                LabelValue.of("first"),
                                LabelValue.of("second"),
                        ]))])

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToProject(projectId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].getAssignee(ProjectId) == projectId
        fieldsForIssue[0].fieldType == FieldType.LABEL_FIELD
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.collect { it.value } as Set == ["first", "second"] as Set
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.every { it.id != null && it.id instanceof UUID }
    }

    def "assigning new value of label field removes old value"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        and:
        def issueId = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, ["first", "second"], FieldType.LABEL_FIELD))

        when:
        fieldAssignmentFacade.assignToField(
                issueId,
                [new LabelFieldAssignmentCommand(
                        fieldId,
                        LabelValues.of([
                                LabelValue.of("new 1"),
                                LabelValue.of("new 2"),
                        ]))])

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToIssue(issueId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].getAssignee(IssueId) == issueId
        fieldsForIssue[0].fieldType == FieldType.LABEL_FIELD
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.collect { it.value } as Set == ["new 1", "new 2"] as Set
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.every { it.id != null && it.id instanceof UUID }
    }

    def "label field value is found when field is updated"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        and:
        def issue1 = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, ["some value"], FieldType.LABEL_FIELD))

        and:
        fieldId = fieldCreation.updateLabelField(fieldId, LabelFieldConfigurationUpdateCommand.builder()
                .allowedLabels(["label"])
                .build())

        when:
        def issue2 = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, ["label"], FieldType.LABEL_FIELD))

        then:
        extractAssignedLabelValues(fieldValuesFacade.findFieldsAssignedToIssue(issue1)) == ["some value"]
        extractAssignedLabelValues(fieldValuesFacade.findFieldsAssignedToIssue(issue2)) == ["label"]
    }

    def "label field value is updated when field is updated"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        and:
        def issue = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, ["label"], FieldType.LABEL_FIELD))

        and:
        fieldId = fieldCreation.updateLabelField(fieldId, LabelFieldConfigurationUpdateCommand.builder()
                .allowedLabel("updated")
                .build())

        when:
        fieldAssignmentFacade.assignToField(
                issue,
                [new LabelFieldAssignmentCommand(
                        fieldId,
                        LabelValues.of([LabelValue.of("updated")]))])

        then:
        extractAssignedLabelValues(fieldValuesFacade.findFieldsAssignedToIssue(issue)) == ["updated"]
    }

    def "no longer used label field definition is removed when field is not used"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()
        def projectId = Fixtures.projectFixture().createNewProject(fieldId)

        and:
        def issue = Fixtures.issueFixture().createNewIssue(
                projectId,
                new FieldToIssueAssignmentCommand(fieldId, ["label"], FieldType.LABEL_FIELD))

        and:
        fieldId = fieldCreation.updateLabelField(fieldId, LabelFieldConfigurationUpdateCommand.builder()
                .allowedLabel("updated")
                .build())

        when:
        fieldAssignmentFacade.assignToField(
                issue,
                [new LabelFieldAssignmentCommand(
                        fieldId,
                        LabelValues.of([LabelValue.of("updated")]))])

        then:
        FieldLookup.findNumberOfFieldsInAnyVersion(fieldId) == 1
    }

    private static List<String> extractAssignedStringValues(Collection<IssueFieldValueDto<?>> values) {
        values.collect { it.getValue(StringValue).value }
    }

    private static List<String> extractAssignedLabelValues(Collection<FieldValueDto<?, ?>> values) {
        values
                .collect() { it.getValue(StringValue).labels.collect { it.value } }
                .flatten()
    }
}

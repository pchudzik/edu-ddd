package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldAssignment
import com.pchudzik.edu.ddd.its.field.FieldCreation
import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.field.LabelValues
import com.pchudzik.edu.ddd.its.field.read.FieldValues
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification

class FieldValues_ATest extends DbSpecification {
    def fieldAssignmentFacade = injector.getInstance(FieldAssignment)
    def fieldCreation = injector.getInstance(FieldCreation)
    def fieldValuesFacade = injector.getInstance(FieldValues)

    def "string field value is assigned to issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issueId = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        when:
        fieldAssignmentFacade.assignStringToIssue(FieldAssignment.StringFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issueId)
                .value("ala ma kota")
                .build())

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToIssue(issueId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].issueId == issueId
        fieldsForIssue[0].fieldType == FieldType.STRING_FIELD
        fieldsForIssue[0].getValue(FieldValues.StringValue).value == "ala ma kota"
        fieldsForIssue[0].getValue(FieldValues.StringValue).id instanceof UUID
    }

    def "assigning new value of string field removes old value"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issueId = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldAssignmentFacade.assignStringToIssue(FieldAssignment.StringFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issueId)
                .value("ala ma kota")
                .build())

        when:
        fieldAssignmentFacade.assignStringToIssue(FieldAssignment.StringFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issueId)
                .value("updated value")
                .build())

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToIssue(issueId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].issueId == issueId
        fieldsForIssue[0].fieldType == FieldType.STRING_FIELD
        fieldsForIssue[0].getValue(FieldValues.StringValue).value == "updated value"
        fieldsForIssue[0].getValue(FieldValues.StringValue).id instanceof UUID
    }

    def "string field value is found when field is updated"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issue1 = Fixtures.issueFixture().createNewIssue(projectId)
        def issue2 = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldAssignmentFacade.assignStringToIssue(FieldAssignment.StringFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue1)
                .value("a")
                .build())

        and:
        fieldId = fieldCreation.updateStringField(fieldId, FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                .minLength(4)
                .maxLength(100)
                .build())

        when:
        fieldAssignmentFacade.assignStringToIssue(FieldAssignment.StringFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue2)
                .value("second value")
                .build())

        then:
        extractAssignedStringValues(fieldValuesFacade.findFieldsAssignedToIssue(issue1)) == ["a"]
        extractAssignedStringValues(fieldValuesFacade.findFieldsAssignedToIssue(issue2)) == ["second value"]
    }

    def "string field value is updated when field is updated"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issue1 = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldAssignmentFacade.assignStringToIssue(FieldAssignment.StringFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue1)
                .value("a")
                .build())

        and:
        fieldId = fieldCreation.updateStringField(fieldId, FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                .minLength(4)
                .maxLength(100)
                .build())

        when:
        fieldAssignmentFacade.assignStringToIssue(FieldAssignment.StringFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue1)
                .value("updated value")
                .build())

        then:
        extractAssignedStringValues(fieldValuesFacade.findFieldsAssignedToIssue(issue1)) == ["updated value"]
    }

    def "no longer used string field definition is removed when field is not used"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issue1 = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldAssignmentFacade.assignStringToIssue(FieldAssignment.StringFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue1)
                .value("a")
                .build())

        and:
        fieldId = fieldCreation.updateStringField(fieldId, FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                .minLength(4)
                .maxLength(100)
                .build())

        when:
        fieldAssignmentFacade.assignStringToIssue(FieldAssignment.StringFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue1)
                .value("updated value")
                .build())

        then:
        FieldLookup.findNumberOfFieldsInAnyVersion(fieldId) == 1
    }


    def "label field value is assigned to issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issueId = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()

        when:
        fieldAssignmentFacade.assignLabelFieldToIssue(FieldAssignment.LabelFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issueId)
                .value(LabelValues.of([
                        LabelValues.LabelValue.of("first"),
                        LabelValues.LabelValue.of("second"),
                ]))
                .build())

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToIssue(issueId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].issueId == issueId
        fieldsForIssue[0].fieldType == FieldType.LABEL_FIELD
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.collect { it.value } as Set == ["first", "second"] as Set
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.every { it.id != null && it.id instanceof UUID }
    }

    def "assigning new value of label field removes old value"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issueId = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()

        and:
        fieldAssignmentFacade.assignLabelFieldToIssue(FieldAssignment.LabelFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issueId)
                .value(LabelValues.of([
                        LabelValues.LabelValue.of("first"),
                        LabelValues.LabelValue.of("second"),
                ]))
                .build())

        when:
        fieldAssignmentFacade.assignLabelFieldToIssue(FieldAssignment.LabelFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issueId)
                .value(LabelValues.of([
                        LabelValues.LabelValue.of("new 1"),
                        LabelValues.LabelValue.of("new 2"),
                ]))
                .build())

        then:
        def fieldsForIssue = fieldValuesFacade.findFieldsAssignedToIssue(issueId)

        and:
        fieldsForIssue.size() == 1
        fieldsForIssue[0].fieldId == fieldId
        fieldsForIssue[0].issueId == issueId
        fieldsForIssue[0].fieldType == FieldType.LABEL_FIELD
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.collect { it.value } as Set == ["new 1", "new 2"]  as Set
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.every { it.id != null && it.id instanceof UUID }
    }

    def "label field value is found when field is updated"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issue1 = Fixtures.issueFixture().createNewIssue(projectId)
        def issue2 = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()

        and:
        fieldAssignmentFacade.assignLabelFieldToIssue(FieldAssignment.LabelFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue1)
                .value(LabelValues.of([LabelValues.LabelValue.of("some value")]))
                .build())

        and:
        fieldId = fieldCreation.updateLabelField(fieldId, FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                .allowedLabels(["label"])
                .build())

        when:
        fieldAssignmentFacade.assignLabelFieldToIssue(FieldAssignment.LabelFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue2)
                .value(LabelValues.of([LabelValues.LabelValue.of("label")]))
                .build())

        then:
        extractAssignedLabelValues(fieldValuesFacade.findFieldsAssignedToIssue(issue1)) == ["some value"]
        extractAssignedLabelValues(fieldValuesFacade.findFieldsAssignedToIssue(issue2)) == ["label"]
    }

    def "label field value is updated when field is updated"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issue = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()

        and:
        fieldAssignmentFacade.assignLabelFieldToIssue(FieldAssignment.LabelFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue)
                .value(LabelValues.of([LabelValues.LabelValue.of("label")]))
                .build())

        and:
        fieldId = fieldCreation.updateLabelField(fieldId, FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                .allowedLabel("updated")
                .build())

        when:
        fieldAssignmentFacade.assignLabelFieldToIssue(FieldAssignment.LabelFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue)
                .value(LabelValues.of([LabelValues.LabelValue.of("updated")]))
                .build())

        then:
        extractAssignedLabelValues(fieldValuesFacade.findFieldsAssignedToIssue(issue)) == ["updated"]
    }

    def "no longer used label field definition is removed when field is not used"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issue = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()

        and:
        fieldAssignmentFacade.assignLabelFieldToIssue(FieldAssignment.LabelFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue)
                .value(LabelValues.of([LabelValues.LabelValue.of("label")]))
                .build())

        and:
        fieldId = fieldCreation.updateLabelField(fieldId, FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                .allowedLabel("updated")
                .build())

        when:
        fieldAssignmentFacade.assignLabelFieldToIssue(FieldAssignment.LabelFieldAssignmentCommand.builder()
                .fieldId(fieldId)
                .issueId(issue)
                .value(LabelValues.of([LabelValues.LabelValue.of("updated")]))
                .build())

        then:
        FieldLookup.findNumberOfFieldsInAnyVersion(fieldId) == 1
    }

    private static List<String> extractAssignedStringValues(Collection<FieldValues.FieldValueDto<?>> values) {
        values.collect { it.getValue(FieldValues.StringValue).value }
    }

    private static List<String> extractAssignedLabelValues(Collection<FieldValues.FieldValueDto<?>> values) {
        values
                .collect() { it.getValue(FieldValues.StringValue).labels.collect { it.value } }
                .flatten()
    }
}

package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldAssignment
import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.field.LabelValues
import com.pchudzik.edu.ddd.its.field.read.FieldValues
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import spock.lang.PendingFeature

class FieldValues_ATest extends DbSpecification {
    def fieldAssignmentFacade = injector.getInstance(FieldAssignment)
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

    @PendingFeature
    def "assigning new value of string field removes old value"() {
        expect:
        false
    }

    @PendingFeature
    def "string field value is found when field is updated"() {
        expect:
        false
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
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.collect { it.value } == ["first", "second"]
        fieldsForIssue[0].getValue(FieldValues.LabelValues).labels.every { it.id != null && it.id instanceof UUID }
    }

    @PendingFeature
    def "assigning new value of label field removes old value"() {
        expect:
        false
    }

    @PendingFeature
    def "label field value is found when field is updated"() {
        expect:
        false
    }
}

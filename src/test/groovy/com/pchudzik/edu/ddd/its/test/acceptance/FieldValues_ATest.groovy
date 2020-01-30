package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldAssignment
import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.field.read.FieldValuesFacade
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification

class FieldValues_ATest extends DbSpecification {
    def fieldAssignmentFacade = injector.getInstance(FieldAssignment)
    def fieldValuesFacade = injector.getInstance(FieldValuesFacade)

    def "new string field is created"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issueId = Fixtures.issueFixture().createNewIssue(projectId)

        and:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        when:
        fieldAssignmentFacade.assignFieldToIssue(FieldAssignment.FieldAssignmentCommand.builder()
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
        fieldsForIssue[0].value.getValue(String) == "ala ma kota"
    }
}

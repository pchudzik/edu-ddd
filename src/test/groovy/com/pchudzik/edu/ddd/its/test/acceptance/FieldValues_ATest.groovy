package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldAssignmentFacade
import com.pchudzik.edu.ddd.its.field.FieldCreationFacade
import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.field.FieldValuesFacade
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.issue.IssueFacade
import com.pchudzik.edu.ddd.its.project.ProjectFacade
import com.pchudzik.edu.ddd.its.project.ProjectId

class FieldValues_ATest extends DbSpecification {

    def fieldCreationFacade = injector.getInstance(FieldCreationFacade)

    def issueFacade = injector.getInstance(IssueFacade)

    def projectFacade = injector.getInstance(ProjectFacade)

    def fieldAssignmentFacade = injector.getInstance(FieldAssignmentFacade)

    def fieldValuesFacade = injector.getInstance(FieldValuesFacade)

    def "new string field is created"() {
        given:
        def projectId = projectFacade.createNewProject(ProjectFacade.ProjectCreationCommand.builder()
                .id(new ProjectId("ABCD"))
                .name("Some Project")
                .description("Some description")
                .build())
        def issueId = issueFacade.createIssue(IssueFacade.IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .build())

        and:
        def fieldId = fieldCreationFacade.createStringField(FieldCreationFacade.StringFieldCreationCommand.builder()
                .fieldName("string field")
                .required(true)
                .minLength(2)
                .maxLength(20)
                .build())

        when:
        fieldAssignmentFacade.assignFieldToIssue(FieldAssignmentFacade.FieldAssignmentCommand.builder()
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

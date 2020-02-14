package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldAssignmentException
import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitions
import com.pchudzik.edu.ddd.its.field.read.FieldValues
import com.pchudzik.edu.ddd.its.field.read.FieldValues.LabelValues
import com.pchudzik.edu.ddd.its.field.read.FieldValues.StringValue
import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.issue.IssueCreation
import com.pchudzik.edu.ddd.its.issue.IssueCreation.FieldToIssueAssignmentCommand
import com.pchudzik.edu.ddd.its.issue.IssueCreation.IssueCreationCommand
import com.pchudzik.edu.ddd.its.issue.id.IssueId
import com.pchudzik.edu.ddd.its.issue.read.IssueRead
import spock.lang.PendingFeature

class IssueCRUD_ATest extends DbSpecification {
    def injector = InjectorFactory.injector()

    def issueFacade = injector.getInstance(IssueCreation)
    def issueReadFacade = injector.getInstance(IssueRead)
    def fieldValues = injector.getInstance(FieldValues)
    def fieldDefinitions = injector.getInstance(FieldDefinitions)

    def "new issue is created"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()

        when:
        def issueId = issueFacade.createIssue(IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .build())

        then:
        def issue = issueReadFacade.findIssue(issueId)
        issue.id == issueId
        issue.title == "some issue"
    }

    @PendingFeature
    def "only fields available for issues in project can be assigned"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def field = Fixtures.fieldFixture().createNewStringField()

        when:
        issueFacade.createIssue(IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .fieldAssignment(new FieldToIssueAssignmentCommand(field, "simple value", FieldType.STRING_FIELD))
                .build())

        then:
        thrown(FieldAssignmentException)
    }

    def "string fields are assigned in when creating issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def field = Fixtures.fieldFixture().createNewStringField()

        when:
        def issueId = issueFacade.createIssue(IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .fieldAssignment(new FieldToIssueAssignmentCommand(field, "simple value", FieldType.STRING_FIELD))
                .build())

        then:
        def fields = fieldValues.findFieldsAssignedToIssue(issueId)
        fields.size() == 1
        fields[0].getAssignee(IssueId) == issueId
        fields[0].getValue(StringValue).value == "simple value"
    }

    def "label fields are assigned when creating issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def field = Fixtures.fieldFixture().createNewLabelField()

        when:
        def issueId = issueFacade.createIssue(IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .fieldAssignment(new FieldToIssueAssignmentCommand(field, ["value1", "value2"], FieldType.LABEL_FIELD))
                .build())

        then:
        def fields = fieldValues.findFieldsAssignedToIssue(issueId)
        fields.size() == 1
        fields[0].getAssignee(IssueId) == issueId
        fields[0].getValue(LabelValues).labels.collect { it.value } == ["value1", "value2"]
    }

    def "issue creation fail when required fields not provided when creating issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def field = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldDefinitions.assignDefaultFields(projectId, [field])

        when:
        def issueId = issueFacade.createIssue(IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .build())

        then:
        thrown(Exception)
    }
}

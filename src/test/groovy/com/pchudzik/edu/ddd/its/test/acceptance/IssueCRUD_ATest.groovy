package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.field.read.FieldValues
import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.issue.IssueCreation
import com.pchudzik.edu.ddd.its.issue.id.IssueId
import com.pchudzik.edu.ddd.its.issue.read.IssueRead
import spock.lang.PendingFeature

class IssueCRUD_ATest extends DbSpecification {
    def injector = InjectorFactory.injector()

    def issueFacade = injector.getInstance(IssueCreation)
    def issueReadFacade = injector.getInstance(IssueRead)
    def fieldValues = injector.getInstance(FieldValues)

    def "new issue is created"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()

        when:
        def issueId = issueFacade.createIssue(IssueCreation.IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .build())

        then:
        def issue = issueReadFacade.findIssue(issueId)
        issue.id == issueId
        issue.title == "some issue"
    }

    def "string fields are assigned in when creating issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def field = Fixtures.fieldFixture().createNewStringField()

        when:
        def issueId = issueFacade.createIssue(IssueCreation.IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .fieldAssignment(new IssueCreation.FieldToIssueAssignmentCommand(field, "simple value", FieldType.STRING_FIELD))
                .build())

        then:
        def issue = issueReadFacade.findIssue(issueId)
        issue.id == issueId

        and:
        def fields = fieldValues.findFieldsAssignedToIssue(issueId)
        fields.size() == 1
        fields[0].getAssignee(IssueId) == issueId
        fields[0].getValue(FieldValues.StringValue).value == "simple value"
    }

    @PendingFeature
    def "label fields are assigned when creating issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def field = Fixtures.fieldFixture().createNewStringField()

        when:
        def issueId = issueFacade.createIssue(IssueCreation.IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .build())

        then:
        false
    }

    @PendingFeature
    def "issue creation fail when required fields not provided when creating issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def field = Fixtures.fieldFixture().createNewStringField()

        when:
        def issueId = issueFacade.createIssue(IssueCreation.IssueCreationCommand.builder()
                .projectId(projectId)
                .title("some issue")
                .fieldAssignment(new IssueCreation.FieldToIssueAssignmentCommand(field, "simple value", FieldType.STRING_FIELD))
                .build())

        then:
        false
        //TODO handle errors somehow
    }
}

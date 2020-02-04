package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitions
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import spock.lang.PendingFeature

class FieldDefinitions_ATest extends DbSpecification {
    def fieldDefinitions = injector.getInstance(FieldDefinitions)

    def "assigns field to project"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        when:
        fieldDefinitions.assignDefaultFields(projectId, [fieldId])

        then:
        def fields = fieldDefinitions.findDefaultFields(projectId)
        fields.size() == 1
        fields[0].id == fieldId
    }

    def "assigns field to issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issueId = Fixtures.issueFixture().createNewIssue(projectId)
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        when:
        fieldDefinitions.assignDefaultFields(issueId, [fieldId])

        then:
        def fields = fieldDefinitions.findDefaultFields(issueId)
        fields.size() == 1
        fields[0].id == fieldId
    }

    def "removed field assignment from project"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldDefinitions.assignDefaultFields(projectId, [fieldId])

        when:
        fieldDefinitions.removeDefaultFields(projectId, fieldId)

        then:
        fieldDefinitions.findDefaultFields(projectId).isEmpty()
    }

    def "removed field assignment from issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issueId = Fixtures.issueFixture().createNewIssue(projectId)
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldDefinitions.assignDefaultFields(issueId, [fieldId])

        when:
        fieldDefinitions.removeDefaultFields(issueId, fieldId)

        then:
        fieldDefinitions.findDefaultFields(issueId).isEmpty()
    }

    @PendingFeature
    def "removed field definition after field is removed from project"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldDefinitions.assignDefaultFields(projectId, [fieldId])

        when:
        fieldDefinitions.removeDefaultFields(projectId, fieldId)

        then:
        FieldLookup.findAllFieldIds().isEmpty()
    }

    @PendingFeature
    def "removed field definition after field is removed from issue"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def issueId = Fixtures.issueFixture().createNewIssue(projectId)
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldDefinitions.assignDefaultFields(issueId, [fieldId])

        when:
        fieldDefinitions.removeDefaultFields(issueId, fieldId)

        then:
        FieldLookup.findAllFieldIds().isEmpty()
    }
}

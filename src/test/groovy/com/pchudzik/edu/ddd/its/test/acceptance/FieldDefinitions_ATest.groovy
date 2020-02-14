package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldId
import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitions
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification

class FieldDefinitions_ATest extends DbSpecification {
    def fieldDefinitions = injector.getInstance(FieldDefinitions)

    def "assigns field to every project project"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        when:
        fieldDefinitions.assignDefaultFields([fieldId])

        then:
        def fields = fieldDefinitions.findDefaultFields()
        fields.size() == 1
        fields[0].id == fieldId
    }


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

    def "detects all required fields in project"() {
        given:
        def projectId = Fixtures.projectFixture().createNewProject()
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldDefinitions.assignDefaultFields(projectId, [fieldId])

        expect:
        fieldDefinitions.findMissingRequiredFields(projectId, [new FieldId(UUID.randomUUID(), 1)]) == [fieldId]
        fieldDefinitions.findMissingRequiredFields(projectId, [fieldId]) == []
    }

    def "detects all required fields for project"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldDefinitions.assignDefaultFields([fieldId])

        expect:
        fieldDefinitions.findMissingRequiredFields([new FieldId(UUID.randomUUID(), 1)]) == [fieldId]
        fieldDefinitions.findMissingRequiredFields([fieldId]) == []
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

    def "removed field assignment"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        and:
        fieldDefinitions.assignDefaultFields([fieldId])

        when:
        fieldDefinitions.removeDefaultFields(fieldId)

        then:
        fieldDefinitions.findDefaultFields().isEmpty()
    }
}

package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldCreation
import com.pchudzik.edu.ddd.its.field.FieldId
import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.field.read.AvailableFields
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification

class FieldCRUD_ATest extends DbSpecification {
    def fieldCreation = injector.getInstance(FieldCreation)
    def fieldRead = injector.getInstance(AvailableFields)

    def "new string field is created"() {
        when:
        def fieldId = fieldCreation.createStringField(FieldCreation.StringFieldCreationCommand.builder()
                .fieldName("string field")
                .required(true)
                .minLength(2)
                .maxLength(20)
                .build())

        then:
        def allFields = fieldRead.listAllFields()

        allFields.size() == 1
        allFields[0].type == FieldType.STRING_FIELD
        allFields[0].id == fieldId
        allFields[0].required == true
        allFields[0].configuration == [
                minLength: 2,
                maxLength: 20
        ]
    }

    def "string field configuration is updated"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        when:
        fieldCreation.updateStringField(fieldId, FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                .required(false)
                .minLength(100)
                .maxLength(200)
                .build())

        then:
        def allFields = fieldRead.listAllFields()

        allFields.size() == 1
        allFields[0].type == FieldType.STRING_FIELD
        allFields[0].id == new FieldId(fieldId.value, fieldId.version + 1)
        allFields[0].required == false
        allFields[0].configuration == [
                minLength: 100,
                maxLength: 200
        ]
    }

    def "new label field is created"() {
        when:
        def fieldId = fieldCreation.createLabelField(FieldCreation.LabelFieldCreationCommand.builder()
                .fieldName("label")
                .fieldDescription("simple required label of two values")
                .required(true)
                .allowedValues(["First", "Second"])
                .build())

        then:
        def allFields = fieldRead.listAllFields()
        allFields.size() == 1
        allFields[0].type == FieldType.LABEL_FIELD
        allFields[0].id == fieldId
        allFields[0].required == true
        allFields[0].configuration.allowedLabels.collect { it.value } == ["First", "Second"]
        allFields[0].configuration.allowedLabels.every { it.id != null && it.id instanceof UUID }
    }

    def "label field configuration is updated"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()

        when:
        fieldCreation.updateLabelField(fieldId, FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                .required(false)
                .allowedLabels(["First", "Second"])
                .build())

        then:
        def allFields = fieldRead.listAllFields()

        allFields.size() == 1
        allFields[0].type == FieldType.LABEL_FIELD
        allFields[0].id == new FieldId(fieldId.value, fieldId.version + 1)
        allFields[0].required == false
        allFields[0].configuration.allowedLabels.collect { it.value } == ["First", "Second"]
        allFields[0].configuration.allowedLabels.every { it.id != null && it.id instanceof UUID }
    }

    def "no longer used string field definitions are removed when not used"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewStringField()

        when:
        fieldCreation.updateStringField(fieldId, FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                .required(false)
                .minLength(100)
                .maxLength(200)
                .build())

        then:
        def allFields = FieldLookup.findAllFieldIds()
        allFields.size() == 1
        allFields[0] == new FieldId(fieldId.value, fieldId.version + 1)
    }

    def "no longer used label field definitions are removed when not used"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()

        when:
        fieldId = fieldCreation.updateLabelField(fieldId, FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                .required(false)
                .allowedLabels(["First", "Second"])
                .build())

        then:
        def allFields = FieldLookup.findAllFieldIds()
        allFields.size() == 1
        allFields[0] == fieldId
    }

    def "no longer used allowed labels are removed when field is updated"() {
        given:
        def fieldId = Fixtures.fieldFixture().createNewLabelField()

        and:
        fieldId = fieldCreation.updateLabelField(fieldId, FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                .required(false)
                .allowedLabels(["first"])
                .build())

        when:
        fieldId = fieldCreation.updateLabelField(fieldId, FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                .required(false)
                .allowedLabels(["updated"])
                .build())

        then:
        def allAllowed = FieldLookup.findAllAllowedLabels()
        allAllowed.size() == 1
        allAllowed[0].fieldId == fieldId
        allAllowed[0].value == "updated"
    }
}

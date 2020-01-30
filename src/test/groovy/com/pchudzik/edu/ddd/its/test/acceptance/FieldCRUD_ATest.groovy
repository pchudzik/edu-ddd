package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldCreation
import com.pchudzik.edu.ddd.its.field.read.FieldReadFacade
import com.pchudzik.edu.ddd.its.field.FieldType
import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification

class FieldCRUD_ATest extends DbSpecification {
    def fieldCreationFacade = injector.getInstance(FieldCreation)
    def fieldReadFacade = injector.getInstance(FieldReadFacade)

    def "new string field is created"() {
        when:
        def fieldId = fieldCreationFacade.createStringField(FieldCreation.StringFieldCreationCommand.builder()
                .fieldName("string field")
                .required(true)
                .minLength(2)
                .maxLength(20)
                .build())

        then:
        def allFields = fieldReadFacade.listAllFields()

        allFields.size() == 1
        allFields[0].type == FieldType.STRING_FIELD
        allFields[0].id == fieldId
        allFields[0].configuration == [
                required : true,
                minLength: 2,
                maxLength: 20
        ]
    }

    def "new label field is created"() {
        when:
        def fieldId = fieldCreationFacade.createLabelField(FieldCreation.LabelFieldCreationCommand.builder()
                .fieldName("label")
                .fieldDescription("simple required label of two values")
                .required(true)
                .allowedValues(["First", "Second"])
                .build())

        then:
        def allFields = fieldReadFacade.listAllFields()
        allFields.size() == 1
        allFields[0].type == FieldType.LABEL_FIELD
        allFields[0].id == fieldId
        allFields[0].configuration.required == true
        allFields[0].configuration.allowedLabels.collect { it.value } == ["First", "Second"]
        allFields[0].configuration.allowedLabels.every {it.id != null && it.id instanceof UUID}
    }
}

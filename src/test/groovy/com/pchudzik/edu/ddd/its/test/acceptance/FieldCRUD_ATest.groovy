package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldCreationFacade
import com.pchudzik.edu.ddd.its.field.FieldReadFacade
import com.pchudzik.edu.ddd.its.field.FieldType

import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification

class FieldCRUD_ATest extends DbSpecification {
    def fieldCreationFacade = injector.getInstance(FieldCreationFacade)
    def fieldReadFacade = injector.getInstance(FieldReadFacade)

    def "new string field is created"() {
        when:
        def fieldId = fieldCreationFacade.createStringField(FieldCreationFacade.StringFieldCreationCommand.builder()
                .fieldName("string field")
                .required(true)
                .minLength(2)
                .maxLength(20)
                .build())

        then:
        def allFields = fieldReadFacade.listAllFields()

        and:
        allFields.size() == 1
        allFields[0].type == FieldType.STRING_FIELD
        allFields[0].id == fieldId
        allFields[0].configuration == [
            required: true,
            minLength: 2,
            maxLength: 20
        ]
    }
}

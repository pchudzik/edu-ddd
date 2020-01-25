package com.pchudzik.edu.ddd.its.field

import spock.lang.Specification

class StringFieldTest extends Specification {
    def "when field configuration is updated version is updated"() {
        given:
        def field = new StringField(new FieldId(), "field")

        when:
        field.required(true)

        then:
        field.fieldId.version == 2

        when:
        field.length(10, 100)

        then:
        field.fieldId.version == 3

    }

    def "non required empty string is accepted"() {
        given:
        def field = new StringField(new FieldId(), "field")
                .required(false)
                .length(0, 128)

        when:
        def value = field.value("ala ma kota")

        then:
        !value.isLeft()
        value.get().value == "ala ma kota"
    }

    def "required field can not be null"() {
        given:
        def field = new StringField(new FieldId(), "field")
                .required(true)
                .length(0, 128)

        when:
        def value = field.value(null)

        then:
        value.swap().get().validationMessages == [RequiredValidator.RequiredValidationMessage.REQUIRED_FIELD]
    }

    def "too short field does not pass validation"() {
        given:
        def field = new StringField(new FieldId(), "field")
                .required(true)
                .length(100, 200)

        when:
        def value = field.value("value")

        then:
        value.swap().get().validationMessages == [new StringField.StringValidationMessage(100, 200)]
    }

    def "too long field does not pass validation"() {
        given:
        def field = new StringField(new FieldId(), "field")
                .required(true)
                .length(2, 3)

        when:
        def value = field.value("super long value")

        then:
        value.swap().get().validationMessages == [new StringField.StringValidationMessage(2, 3)]
    }
}

package com.pchudzik.edu.ddd.its.field

import spock.lang.Specification

class StringFieldTest extends Specification {
    def "when field configuration is updated version is updated"() {
        given:
        def field = new StringField("field")

        when:
        field.applyConfiguration(FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                .required(true)
                .build())

        then:
        field.fieldId.version == 2

        when:
        field.applyConfiguration(FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                .minLength(100)
                .build())

        then:
        field.fieldId.version == 3
    }

    def "non required empty string is accepted"() {
        given:
        def field = new StringField("field")
                .applyConfiguration(FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                        .minLength(0)
                        .maxLength(128)
                        .required(false)
                        .build())

        when:
        def value = field.value("ala ma kota")

        then:
        !value.isLeft()
        value.get().value == "ala ma kota"
    }

    def "when length is not specified any string length will match"() {
        given:
        def field = new StringField("field")
                .applyConfiguration(FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                        .required(false)
                        .build())

        when:
        def snapshot = field.getSnapshot()

        then:
        snapshot.configuration.minLength == 0
        snapshot.configuration.maxLength == Integer.MAX_VALUE
    }

    def "string min length must be less than max length"() {
        given:
        def field = new StringField("field")

        when:
        field.applyConfiguration(FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                .minLength(100)
                .maxLength(10)
                .build())

        then:
        thrown IllegalArgumentException
    }

    def "required field can not be null"() {
        given:
        def field = new StringField("field")
                .applyConfiguration(FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                        .required(true)
                        .minLength(0)
                        .maxLength(128)
                        .build())

        when:
        def value = field.value(null)

        then:
        value.swap().get().validationMessages == [StringField.RequiredValidator.RequiredValidationMessage.REQUIRED_FIELD]
    }

    def "too short field does not pass validation"() {
        given:
        def field = new StringField("field")
                .applyConfiguration(FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                        .required(true)
                        .minLength(100)
                        .maxLength(200)
                        .build())

        when:
        def value = field.value("value")

        then:
        value.swap().get().validationMessages.size() == 1
        value.swap().get().validationMessages[0].messageKey == "Value must be between 100 and 200 characters"
    }

    def "too long field does not pass validation"() {
        given:
        def field = new StringField("field")
                .applyConfiguration(FieldCreation.StringFieldConfigurationUpdateCommand.builder()
                        .required(true)
                        .minLength(2)
                        .maxLength(3)
                        .build())

        when:
        def value = field.value("super long value")

        then:
        value.swap().get().validationMessages.size() == 1
        value.swap().get().validationMessages[0].messageKey == "Value must be between 2 and 3 characters"
    }
}

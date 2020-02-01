package com.pchudzik.edu.ddd.its.field

import spock.lang.Specification

class LabelFieldTest extends Specification {
    def "any label can be assigned"() {
        given:
        def field = new LabelField("label")

        and:
        def label2 = LabelValues.LabelValue.of("second")
        def label1 = LabelValues.LabelValue.of("first")

        when:
        def value = field.value(LabelValues.of([label1, label2]))

        then:
        value.isRight()
        value.get().value == LabelValues.of([label1, label2])
    }

    def "only allowed labels can be assigned"() {
        given:
        def field = new LabelField("label")
                .applyConfiguration(FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                        .allowedLabel("allowed")
                        .build())

        and:
        def label = LabelValues.LabelValue.of("other")

        when:
        def value = field.value(LabelValues.of([label]))

        then:
        value.isLeft()
        !value.swap().get().valid
    }

    def "allowed labels comparison is case insensitive"() {
        given:
        def field = new LabelField("label")
                .applyConfiguration(FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                        .allowedLabel("allowed")
                        .build())

        and:
        def label = LabelValues.LabelValue.of("aLLoWeD")

        when:
        def value = field.value(LabelValues.of([label]))

        then:
        value.isRight()
    }

    def "not allowed labels message is formatted"() {
        given:
        def field = new LabelField("label")
                .applyConfiguration(FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                        .allowedLabels(["First", "Second"])
                        .build())

        and:
        def notAllowed = LabelValues.LabelValue.of("Third")
        def allowed = LabelValues.LabelValue.of("Second")

        when:
        def value = field.value(LabelValues.of([notAllowed, allowed]))

        then:
        value.isLeft()
        value.swap().get().validationMessages[0].messageKey == "Allowed labels are First, Second not allowed values Third"
    }

    def "label can be required"() {
        given:
        def field = new LabelField("label")
                .applyConfiguration(FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                        .required(true)
                        .build())

        when:
        def value = field.value(LabelValues.empty())

        then:
        value.isLeft()
        !value.swap().get().valid
        value.swap().get().validationMessages == [LabelField.LabelIsRequiredError.LABEL_IS_REQUIRED_ERROR]
    }

    def "when field configuration is updated version is updated"() {
        given:
        def field = new LabelField("field")

        when:
        field.applyConfiguration(FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                .required(true)
                .build())

        then:
        field.fieldId.version == 2

        when:
        field.applyConfiguration(FieldCreation.LabelFieldConfigurationUpdateCommand.builder()
                .allowedLabel("abc")
                .build())

        then:
        field.fieldId.version == 3

    }
}

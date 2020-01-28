package com.pchudzik.edu.ddd.its.field

import spock.lang.Specification

class LabelFieldTest extends Specification {
    def "any label can be assigned"() {
        given:
        def field = new LabelField("label")

        and:
        def label2 = new LabelField.LabelValue("second")
        def label1 = new LabelField.LabelValue("first")

        when:
        def value = field.value(LabelField.LabelValues.of(label1, label2))

        then:
        value.isRight()
        value.get().value == LabelField.LabelValues.of(label1, label2)
    }

    def "only allowed labels can be assigned"() {
        given:
        def allowedLabel = new LabelField.LabelValue("allowed")
        def field = new LabelField("label")
                .allowedValues([allowedLabel])

        and:
        def label = new LabelField.LabelValue("other")

        when:
        def value = field.value(LabelField.LabelValues.of(label))

        then:
        value.isLeft()
        !value.swap().get().valid
        value.swap().get().validationMessages == [new LabelField.LabelNotAllowedValidationError(new LabelField.LabelValues([allowedLabel]), new LabelField.LabelValues([label]))]
    }

    def "allowed labels comparison is case insensitive"() {
        given:
        def allowedLabel = new LabelField.LabelValue("Allowed")
        def field = new LabelField("label")
                .allowedValues([allowedLabel])

        and:
        def label = new LabelField.LabelValue("aLLoWeD")

        when:
        def value = field.value(LabelField.LabelValues.of(label))

        then:
        value.isRight()
    }

    def "not allowed labels message is formatted"() {
        given:
        def allowedLabel1 = new LabelField.LabelValue("First")
        def allowedLabel2 = new LabelField.LabelValue("Second")
        def field = new LabelField("label")
                .allowedValues([allowedLabel1, allowedLabel2])

        and:
        def notAllowed = new LabelField.LabelValue("Third")
        def allowed = new LabelField.LabelValue("Second")

        when:
        def value = field.value(LabelField.LabelValues.of(notAllowed, allowed))

        then:
        value.isLeft()
        value.swap().get().validationMessages[0].messageKey == "Allowed labels are First, Second not allowed values Third"
    }

    def "label can be required"() {
        given:
        def field = new LabelField("label").required(true)

        when:
        def value = field.value(LabelField.LabelValues.empty())

        then:
        value.isLeft()
        !value.swap().get().valid
        value.swap().get().validationMessages == [LabelField.LabelIsRequiredError.LABEL_IS_REQUIRED_ERROR]
    }

    def "when field configuration is updated version is updated"() {
        given:
        def field = new LabelField("field")

        when:
        field.required(true)

        then:
        field.fieldId.version == 2

        when:
        field.allowedValues([new LabelField.LabelValue("abc")])

        then:
        field.fieldId.version == 3

    }
}

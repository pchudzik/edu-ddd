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
        def allowedLabel = LabelValues.LabelValue.of("allowed")
        def field = new LabelField("label")
                .allowedValues([allowedLabel])

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
        def allowedLabel =LabelValues.LabelValue.of("Allowed")
        def field = new LabelField("label")
                .allowedValues([allowedLabel])

        and:
        def label = LabelValues.LabelValue.of("aLLoWeD")

        when:
        def value = field.value(LabelValues.of([label]))

        then:
        value.isRight()
    }

    def "not allowed labels message is formatted"() {
        given:
        def allowedLabel1 =LabelValues.LabelValue.of("First")
        def allowedLabel2 = LabelValues.LabelValue.of("Second")
        def field = new LabelField("label")
                .allowedValues([allowedLabel1, allowedLabel2])

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
        def field = new LabelField("label").required(true)

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
        field.required(true)

        then:
        field.fieldId.version == 2

        when:
        field.allowedValues([LabelValues.LabelValue.of("abc")])

        then:
        field.fieldId.version == 3

    }
}

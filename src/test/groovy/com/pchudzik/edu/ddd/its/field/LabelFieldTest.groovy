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
        def label = new LabelField.LabelValue("second")

        when:
        def value = field.value(LabelField.LabelValues.of(label))

        then:
        value.isLeft()
        !value.swap().get().valid
        value.swap().get().validationMessages == [new LabelField.LabelNotAllowedValidationError([allowedLabel], [label])]
    }

    def "label can be required"() {
        given:
        def allowedLabel = new LabelField.LabelValue("allowed")
        def field = new LabelField("label")
                .allowedValues([allowedLabel])

        and:
        def label = new LabelField.LabelValue("second")

        when:
        def value = field.value(LabelField.LabelValues.empty())

        then:
        value.isLeft()
        !value.swap().get().valid
        value.swap().get().validationMessages == [new LabelField.LabelIsRequiredError()]
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

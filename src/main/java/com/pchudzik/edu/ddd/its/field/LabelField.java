package com.pchudzik.edu.ddd.its.field;

import io.vavr.control.Either;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

class LabelField implements CustomField<LabelField.LabelValues> {
    @Getter
    private FieldId fieldId;

    private FieldName fieldName;

    private boolean required = false;
    private LabelValues allowedLabels = LabelValues.empty();

    public LabelField(String name) {
        this.fieldId = new FieldId();
        this.fieldName = new FieldName(name);
    }

    public LabelField required(boolean required) {
        this.required = required;
        this.fieldId = fieldId.nextVersion();
        return this;
    }

    public LabelField allowedValues(Collection<LabelValue> values) {
        this.allowedLabels = new LabelValues(values);
        this.fieldId = fieldId.nextVersion();
        return this;
    }

    @Override
    public <A> Either<FieldValidator.ValidationResult, ? extends FieldValue<LabelValues>> value(LabelValues value) {
        FieldValidator.ValidationResult valid = new RequiredLabelValidator(required)
                .and(new FixedValuesValidator(allowedLabels))
                .isValid(value);

        if (valid.isValid()) {
            return Either.right(null);
        }

        return Either.left(valid);
    }

    @EqualsAndHashCode
    static class LabelValues {
        private final List<LabelValue> labels;

        private LabelValues(Collection<LabelValue> labels) {
            this.labels = new ArrayList<>(labels);
        }

        public static LabelValues of(LabelValue... labels) {
            return new LabelValues(asList(labels));
        }

        public static LabelValues empty() {
            return new LabelValues(emptyList());
        }

        private boolean isEmpty() {
            return labels.isEmpty();
        }

        private LabelValues findNotAllowed(LabelValues value) {
            return empty();
        }
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class LabelValue {
        private final UUID id;
        private final String value;

        public LabelValue(String value) {
            this(UUID.randomUUID(), value);
        }
    }

    static class LabelNotAllowedValidationError implements FieldValidator.ValidationMessage {
        LabelNotAllowedValidationError(LabelValues allowedValues, LabelValues notAllowed) {
        }

        @Override
        public String getMessageKey() {
            return null;
        }
    }

    static class LabelIsRequiredError implements FieldValidator.ValidationMessage {

        @Override
        public String getMessageKey() {
            return null;
        }
    }

    @RequiredArgsConstructor
    private static class RequiredLabelValidator implements FieldValidator<LabelValues> {
        private final boolean required;

        @Override
        public ValidationResult isValid(LabelValues value) {
            if (required && value.isEmpty()) {
                return new SimpleValidationError(new LabelIsRequiredError());
            }
            return FieldValidator.noError();
        }
    }

    @RequiredArgsConstructor
    private static class FixedValuesValidator implements FieldValidator<LabelValues> {
        private final LabelValues allowedLabels;

        @Override
        public ValidationResult isValid(LabelValues value) {
            LabelValues notAllowed = allowedLabels.findNotAllowed(value);

            if (notAllowed.isEmpty()) {
                return FieldValidator.noError();
            }

            return new SimpleValidationError(new LabelNotAllowedValidationError(allowedLabels, notAllowed));
        }
    }
}

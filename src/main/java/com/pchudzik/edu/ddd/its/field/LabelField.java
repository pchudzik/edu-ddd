package com.pchudzik.edu.ddd.its.field;

import io.vavr.control.Either;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            return Either.right(new LabelFieldValue(fieldId, new LabelValues(value.labels)));
        }

        return Either.left(valid);
    }

    enum LabelIsRequiredError implements FieldValidator.ValidationMessage {
        LABEL_IS_REQUIRED_ERROR {
            @Override
            public String getMessageKey() {
                return "Label is required";
            }
        }
    }

    @RequiredArgsConstructor
    private static class LabelFieldValue implements FieldValue<LabelValues> {
        @Getter
        private final FieldId fieldId;
        @Getter
        private final LabelValues value;
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

        private LabelValues findNotAllowed(LabelValues other) {
            Set<String> allowedValues = labels.stream()
                    .map(l -> StringUtils.lowerCase(l.value))
                    .collect(Collectors.toSet());
            List<LabelValue> notAllowedValues = other.stream()
                    .filter(l -> !allowedValues.contains(StringUtils.lowerCase(l.value)))
                    .collect(Collectors.toList());
            return new LabelValues(notAllowedValues);
        }

        private Stream<LabelValue> stream() {
            return labels.stream();
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

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class LabelNotAllowedValidationError implements FieldValidator.ValidationMessage {
        private final LabelValues allowedValues;
        private final LabelValues notAllowedValues;

        @Override
        public String getMessageKey() {
            return "Allowed labels are " + formatLabels(allowedValues) + " not allowed values " + formatLabels(notAllowedValues);
        }

        private String formatLabels(LabelValues notAllowedValues) {
            return notAllowedValues.stream()
                    .map(l -> l.value)
                    .collect(Collectors.joining(", "));
        }
    }

    @RequiredArgsConstructor
    private static class RequiredLabelValidator implements FieldValidator<LabelValues> {
        private final boolean required;

        @Override
        public ValidationResult isValid(LabelValues value) {
            if (required && value.isEmpty()) {
                return new SimpleValidationError(LabelIsRequiredError.LABEL_IS_REQUIRED_ERROR);
            }
            return FieldValidator.noError();
        }
    }

    @RequiredArgsConstructor
    private static class FixedValuesValidator implements FieldValidator<LabelValues> {
        private final LabelValues allowedLabels;

        @Override
        public ValidationResult isValid(LabelValues value) {
            if (allowedLabels.isEmpty()) {
                return FieldValidator.noError();
            }

            LabelValues notAllowed = allowedLabels.findNotAllowed(value);

            if (notAllowed.isEmpty()) {
                return FieldValidator.noError();
            }

            return new SimpleValidationError(new LabelNotAllowedValidationError(allowedLabels, notAllowed));
        }
    }
}

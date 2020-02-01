package com.pchudzik.edu.ddd.its.field;

import io.vavr.control.Either;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

class LabelField implements Field<LabelValues> {
    @Getter
    private FieldId fieldId;

    private FieldName fieldName;

    private boolean required = false;
    private AllowableLabelValues allowedLabels = AllowableLabelValues.empty();

    public LabelField(String name) {
        this.fieldId = new FieldId();
        this.fieldName = new FieldName(name);
    }

    public LabelField(FieldId fieldId, FieldName fieldName, boolean required, Collection<IdentifiableLabelValue> allowedValues) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.required = required;
        this.allowedLabels = new AllowableLabelValues(allowedValues);
    }

    public LabelField required(boolean required) {
        this.required = required;
        this.fieldId = fieldId.nextVersion();
        return this;
    }

    public LabelField allowedValues(Collection<LabelValues.LabelValue> values) {
        this.allowedLabels = new AllowableLabelValues(values.stream()
                .map(l -> new IdentifiableLabelValue(UUID.randomUUID(), l.getValue()))
                .collect(Collectors.toList()));
        this.fieldId = fieldId.nextVersion();
        return this;
    }

    @Override
    public Either<FieldValidator.ValidationResult, ? extends FieldValue<LabelValues>> value(LabelValues value) {
        FieldValidator.ValidationResult valid = new RequiredLabelValidator(required)
                .and(new FixedValuesValidator(allowedLabels))
                .isValid(value);

        if (valid.isValid()) {
            return Either.right(new LabelFieldValue(fieldId, LabelValues.of(value.getLabels())));
        }

        return Either.left(valid);
    }

    public LabelFieldSnapshot getSnapshot() {
        return LabelFieldSnapshot.builder()
                .fieldId(fieldId)
                .fieldName(fieldName.getFieldName())
                .fieldDescription(fieldName.getFieldDescription())
                .required(required)
                .allowedValues(allowedLabels.getIdentifiableLabels().stream()
                        .map(l -> new LabelFieldSnapshot.Label(l.getId(), l.getValue()))
                        .collect(Collectors.toList()))
                .build();
    }

    enum LabelIsRequiredError implements FieldValidator.ValidationMessage {
        LABEL_IS_REQUIRED_ERROR {
            @Override
            public String getMessageKey() {
                return "Label is required";
            }
        }
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    static class LabelFieldSnapshot {
        private final FieldId fieldId;
        private final String fieldName;
        private final String fieldDescription;
        private final boolean required;
        private final List<Label> allowedValues;

        @Getter
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        static class Label {
            private final UUID id;
            private final String value;
        }
    }

    @RequiredArgsConstructor
    private static class LabelFieldValue implements FieldValue<LabelValues> {
        @Getter
        private final FieldId fieldId;
        @Getter
        private final LabelValues value;
    }

    private static class AllowableLabelValues implements LabelValues {
        @Getter
        private final List<IdentifiableLabelValue> labels;

        private AllowableLabelValues(Collection<IdentifiableLabelValue> labels) {
            this.labels = new ArrayList<>(labels);
        }

        public static AllowableLabelValues empty() {
            return new AllowableLabelValues(emptyList());
        }

        public List<IdentifiableLabelValue> getIdentifiableLabels() {
            return labels;
        }

        private LabelValues findNotAllowed(LabelValues other) {
            var allowedValues = labels.stream()
                    .map(l -> StringUtils.lowerCase(l.getValue()))
                    .collect(Collectors.toSet());
            var notAllowedValues = other.getLabels().stream()
                    .filter(l -> !allowedValues.contains(StringUtils.lowerCase(l.getValue())))
                    .collect(Collectors.toList());
            return LabelValues.of(notAllowedValues);
        }
    }

    @Getter
    @RequiredArgsConstructor
    static class IdentifiableLabelValue implements LabelValues.LabelValue {
        private final UUID id;
        private final String value;
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
                    .map(LabelValues.LabelValue::getValue)
                    .collect(Collectors.joining(", "));
        }
    }

    @RequiredArgsConstructor
    private static class RequiredLabelValidator implements FieldValidator<LabelValues> {
        private final boolean required;

        @Override
        public ValidationResult isValid(LabelValues value) {
            if (required && value.getLabels().isEmpty()) {
                return new SimpleValidationError(LabelIsRequiredError.LABEL_IS_REQUIRED_ERROR);
            }
            return FieldValidator.noError();
        }
    }

    @RequiredArgsConstructor
    private static class FixedValuesValidator implements FieldValidator<LabelValues> {
        private final AllowableLabelValues allowedLabels;

        @Override
        public ValidationResult isValid(LabelValues value) {
            if (allowedLabels.isEmpty()) {
                return FieldValidator.noError();
            }

            var notAllowed = allowedLabels.findNotAllowed(value);

            if (notAllowed.isEmpty()) {
                return FieldValidator.noError();
            }

            return new SimpleValidationError(new LabelNotAllowedValidationError(allowedLabels, notAllowed));
        }
    }
}

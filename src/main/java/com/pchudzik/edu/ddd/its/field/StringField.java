package com.pchudzik.edu.ddd.its.field;

import io.vavr.control.Either;
import lombok.*;

import java.util.Optional;

class StringField implements CustomField<String> {
    @Getter
    private final FieldId fieldId;

    @Getter
    private FieldName fieldName;

    @Getter
    private FieldVersion fieldVersion;

    private StringFieldConfiguration configuration;

    public StringField(FieldId fieldId, String fieldName) {
        this(
                fieldId, new FieldName(fieldName),
                RequiredValidator.NOT_REQUIRED, StringFieldConfiguration.ZERO, StringFieldConfiguration.EVERYTHING);
    }

    public StringField(FieldId fieldId, FieldName fieldName, boolean required, int minLength, int maxLength) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.fieldVersion = new FieldVersion(1);
        this.configuration = new StringFieldConfiguration(required, minLength, maxLength);
    }

    public StringField required(boolean required) {
        this.configuration = configuration.required(required);
        this.fieldVersion = fieldVersion.next();
        return this;
    }

    public StringField length(int min, int max) {
        this.configuration = configuration.length(min, max);
        this.fieldVersion = fieldVersion.next();
        return this;
    }

    public Either<FieldValidator.ValidationResult, StringFieldValue> value(String value) {
        FieldValidator.ValidationResult validationResult = configuration.getValidator().isValid(value);
        if (validationResult.isValid()) {
            return Either.right(new StringFieldValue(fieldId, fieldVersion, value));
        } else {
            return Either.left(validationResult);
        }
    }

    public StringFieldSnapshot getSnapshot() {
        return StringFieldSnapshot.builder()
                .fieldId(fieldId)
                .fieldVersion(fieldVersion)
                .fieldName(fieldName.getFieldName())
                .fieldDescription(fieldName.getFieldDescription())
                .configuration(configuration.getSnapshot())
                .build();
    }

    @Getter(AccessLevel.PACKAGE)
    @Builder(access = AccessLevel.PRIVATE)
    static class StringFieldSnapshot {
        private final FieldId fieldId;
        private final FieldVersion fieldVersion;
        private final String fieldName;
        private final String fieldDescription;
        private final StringFieldConfigurationSnapshot configuration;

        @Getter(AccessLevel.PACKAGE)
        @Builder(access = AccessLevel.PRIVATE)
        static class StringFieldConfigurationSnapshot {
            private final boolean isRequired;
            private final int minLength;
            private final int maxLength;
        }
    }

    @RequiredArgsConstructor
    private static class StringFieldConfiguration {
        private static final int ZERO = 0;
        private static final int EVERYTHING = Integer.MAX_VALUE;

        private final boolean isRequired;
        private final int minLength;
        private final int maxLength;

        public StringFieldConfiguration required(boolean required) {
            return new StringFieldConfiguration(required, minLength, maxLength);
        }

        public StringFieldConfiguration length(int min, int max) {
            return new StringFieldConfiguration(isRequired, min, max);
        }

        public FieldValidator<String> getValidator() {
            return new RequiredValidator<String>(isRequired)
                    .and(new MinLengthValidator())
                    .and(new MaxLengthValidator());
        }

        public StringFieldSnapshot.StringFieldConfigurationSnapshot getSnapshot() {
            return StringFieldSnapshot.StringFieldConfigurationSnapshot.builder()
                    .isRequired(isRequired)
                    .minLength(minLength)
                    .maxLength(maxLength)
                    .build();
        }

        private class MinLengthValidator implements FieldValidator<String> {
            @Override
            public ValidationResult isValid(String value) {
                boolean isValid = Optional
                        .ofNullable(value)
                        .map(v -> v.length() >= minLength)
                        .orElse(true);

                return isValid
                        ? FieldValidator.noError()
                        : new SimpleValidationError(new StringValidationMessage(minLength, maxLength));
            }
        }

        private class MaxLengthValidator implements FieldValidator<String> {
            @Override
            public ValidationResult isValid(String value) {
                boolean isValid = Optional
                        .ofNullable(value)
                        .map(v -> v.length() < maxLength)
                        .orElse(true);

                return isValid
                        ? FieldValidator.noError()
                        : new SimpleValidationError(new StringValidationMessage(minLength, maxLength));
            }
        }
    }

    @RequiredArgsConstructor
    private static class StringFieldValue implements FieldValue<String> {
        @Getter
        private final FieldId fieldId;

        @Getter
        private final FieldVersion configurationFieldVersion;

        @Getter
        private final String value;
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class StringValidationMessage implements FieldValidator.ValidationMessage {
        private final int min;
        private final int max;

        @Override
        public String getMessageKey() {
            return "Value must be between " + min + " and " + max + " characters";
        }
    }
}

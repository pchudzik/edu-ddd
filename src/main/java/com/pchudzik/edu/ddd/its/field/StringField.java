package com.pchudzik.edu.ddd.its.field;

import io.vavr.control.Either;
import lombok.*;

import javax.annotation.Nullable;
import java.util.Optional;

class StringField implements Field<String> {
    @Getter
    private FieldId fieldId;

    private FieldName fieldName;

    private StringFieldConfiguration configuration;

    public StringField(String fieldName) {
        this(
                new FieldId(), new FieldName(fieldName),
                RequiredValidator.NOT_REQUIRED, StringFieldConfiguration.ZERO, StringFieldConfiguration.EVERYTHING);
    }

    public StringField(FieldId fieldId, FieldName fieldName, boolean required, Integer minLength, Integer maxLength) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.configuration = new StringFieldConfiguration(required, minLength, maxLength);
    }

    public <A> Either<FieldValidator.ValidationResult, StringFieldValue<A>> value(String value) {
        var validationResult = configuration.getValidator().isValid(value);
        if (validationResult.isValid()) {
            return Either.right(new StringFieldValue<>(fieldId, value));
        } else {
            return Either.left(validationResult);
        }
    }

    public StringFieldSnapshot getSnapshot() {
        return StringFieldSnapshot.builder()
                .fieldId(fieldId)
                .fieldName(fieldName.getFieldName())
                .fieldDescription(fieldName.getFieldDescription())
                .configuration(configuration.getSnapshot())
                .build();
    }

    public StringField applyConfiguration(FieldCreation.StringFieldConfigurationUpdateCommand cfg) {
        configuration = configuration
                .required(cfg.isRequired())
                .length(cfg.getMinLength(), cfg.getMaxLength());
        fieldId = fieldId.nextVersion();
        return this;
    }

    @Getter(AccessLevel.PACKAGE)
    @Builder(access = AccessLevel.PRIVATE)
    static class StringFieldSnapshot {
        private final FieldId fieldId;
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

    private static class StringFieldConfiguration {
        private static final int ZERO = 0;
        private static final int EVERYTHING = Integer.MAX_VALUE;

        private final boolean isRequired;
        private final Length length;

        private StringFieldConfiguration(boolean isRequired, Integer minLength, Integer maxLength) {
            this.isRequired = isRequired;
            this.length = Length.of(minLength, maxLength);
        }

        public StringFieldConfiguration required(boolean required) {
            return new StringFieldConfiguration(required, this.length.minLength, this.length.maxLength);
        }

        public StringFieldConfiguration length(@Nullable Integer min, @Nullable Integer max) {
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
                    .minLength(length.minLength)
                    .maxLength(length.maxLength)
                    .build();
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class Length {
            private final int minLength;
            private final int maxLength;

            public static Length of(@Nullable Integer min, @Nullable Integer max) {
                var minLength = Optional.ofNullable(min).orElse(ZERO);
                var maxLength = Optional.ofNullable(max).orElse(EVERYTHING);

                if (minLength > maxLength) {
                    throw new IllegalArgumentException(String.format(
                            "Min string length of %s can not be smaller than max length of %s",
                            minLength, maxLength));
                }

                return new Length(minLength, maxLength);
            }
        }

        private class MinLengthValidator implements FieldValidator<String> {
            @Override
            public ValidationResult isValid(String value) {
                boolean isValid = Optional
                        .ofNullable(value)
                        .map(v -> v.length() >= length.minLength)
                        .orElse(true);

                return isValid
                        ? FieldValidator.noError()
                        : new SimpleValidationError(new StringValidationMessage(length));
            }
        }

        private class MaxLengthValidator implements FieldValidator<String> {
            @Override
            public ValidationResult isValid(String value) {
                boolean isValid = Optional
                        .ofNullable(value)
                        .map(v -> v.length() < length.maxLength)
                        .orElse(true);

                return isValid
                        ? FieldValidator.noError()
                        : new SimpleValidationError(new StringValidationMessage(length));
            }
        }
    }

    @RequiredArgsConstructor
    private static class StringFieldValue<A> implements FieldValue<String> {
        @Getter
        private final FieldId fieldId;

        @Getter
        private final String value;
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class StringValidationMessage implements FieldValidator.ValidationMessage {
        private final StringFieldConfiguration.Length length;

        @Override
        public String getMessageKey() {
            return String.format(
                    "Value must be between %s and %s characters",
                    length.minLength, length.maxLength);
        }
    }

    @RequiredArgsConstructor
    static
    class RequiredValidator<T> implements FieldValidator<T> {
        public static final boolean REQUIRED = true;
        public static final boolean NOT_REQUIRED = false;

        private final boolean isRequired;

        @Override
        public ValidationResult isValid(T value) {
            boolean isValid = value != null || !isRequired;
            return isValid ? FieldValidator.noError() : new SimpleValidationError(RequiredValidationMessage.REQUIRED_FIELD);
        }

        public enum RequiredValidationMessage implements ValidationMessage {
            REQUIRED_FIELD {
                @Override
                public String getMessageKey() {
                    return "Field is required";
                }
            }
        }
    }
}

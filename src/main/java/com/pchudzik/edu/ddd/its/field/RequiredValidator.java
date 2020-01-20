package com.pchudzik.edu.ddd.its.field;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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

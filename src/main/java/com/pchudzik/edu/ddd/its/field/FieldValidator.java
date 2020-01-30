package com.pchudzik.edu.ddd.its.field;

import java.util.Collections;
import java.util.List;

interface FieldValidator<T> {
    static ValidationResult noError() {
        return Collections::emptyList;
    }

    ValidationResult isValid(T value);

    default FieldValidator<T> and(FieldValidator<T> other) {
        return new FieldCompositeValidator<>(this, other);
    }

    interface ValidationResult {
        List<ValidationMessage> getValidationMessages();

        default boolean isValid() {
            return getValidationMessages().isEmpty();
        }
    }

    interface ValidationMessage {
        String getMessageKey();
    }
}

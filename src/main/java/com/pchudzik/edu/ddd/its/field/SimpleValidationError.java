package com.pchudzik.edu.ddd.its.field;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
class SimpleValidationError implements FieldValidator.ValidationResult {
    private final FieldValidator.ValidationMessage message;

    @Override
    public List<FieldValidator.ValidationMessage> getValidationMessages() {
        return Collections.singletonList(message);
    }
}

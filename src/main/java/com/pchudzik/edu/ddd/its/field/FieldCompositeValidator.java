package com.pchudzik.edu.ddd.its.field;

import io.vavr.collection.Stream;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
class FieldCompositeValidator<T> implements FieldValidator<T> {
    private final FieldValidator<T> a;
    private final FieldValidator<T> b;

    @Override
    public ValidationResult isValid(T value) {
        return new ComposedValidationResult(
                a.isValid(value),
                b.isValid(value));
    }

    @RequiredArgsConstructor
    private static class ComposedValidationResult implements ValidationResult {
        private final ValidationResult a;
        private final ValidationResult b;

        @Override
        public List<ValidationMessage> getValidationMessages() {
            return Stream
                    .concat(
                            a.getValidationMessages(),
                            b.getValidationMessages())
                    .collect(toList());
        }
    }
}

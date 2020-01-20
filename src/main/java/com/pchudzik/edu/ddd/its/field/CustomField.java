package com.pchudzik.edu.ddd.its.field;

import io.vavr.control.Either;

interface CustomField<T> {
    FieldId getFieldId();

    FieldName getFieldName();

    FieldVersion getFieldVersion();

    Either<FieldValidator.ValidationResult, ? extends FieldValue<T>> value(T value);

}

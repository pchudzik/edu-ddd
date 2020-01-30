package com.pchudzik.edu.ddd.its.field;

import io.vavr.control.Either;

interface Field<T> {
    FieldId getFieldId();

    <A> Either<FieldValidator.ValidationResult, ? extends FieldValue<T>> value(T value);
}

package com.pchudzik.edu.ddd.its.field;

import io.vavr.control.Either;

interface CustomField<T> {
    FieldId getFieldId();

    FieldName getFieldName();

    FieldVersion getFieldVersion();

    <A> Either<FieldValidator.ValidationResult, ? extends FieldValue<A, T>> value(A assignee, T value);

}

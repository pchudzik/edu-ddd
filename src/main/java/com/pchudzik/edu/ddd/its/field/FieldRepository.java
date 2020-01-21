package com.pchudzik.edu.ddd.its.field;

interface FieldRepository {
    <T> CustomField<T> findOne(FieldId fieldId);
}

package com.pchudzik.edu.ddd.its.field;

public interface FieldValue<T> {
    FieldId getFieldId();

    FieldVersion getConfigurationFieldVersion();

    T getValue();
}

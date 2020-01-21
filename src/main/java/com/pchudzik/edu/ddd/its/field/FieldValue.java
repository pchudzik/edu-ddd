package com.pchudzik.edu.ddd.its.field;

public interface FieldValue<A, V> {

    FieldId getFieldId();

    FieldVersion getFieldVersion();

    A getAssignee();

    V getValue();
}

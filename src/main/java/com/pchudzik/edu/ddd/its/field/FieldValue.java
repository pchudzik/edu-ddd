package com.pchudzik.edu.ddd.its.field;

interface FieldValue<V> {

    FieldId getFieldId();

    V getValue();
}

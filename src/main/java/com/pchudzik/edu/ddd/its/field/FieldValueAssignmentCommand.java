package com.pchudzik.edu.ddd.its.field;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FieldValueAssignmentCommand<V> {
    private final FieldId fieldId;
    private final V value;
    private final FieldType fieldType;
}

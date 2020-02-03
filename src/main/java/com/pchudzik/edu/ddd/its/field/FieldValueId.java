package com.pchudzik.edu.ddd.its.field;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
class FieldValueId {
    @Getter
    private final UUID valueId;
    private final FieldId fieldId;

    public FieldValueId(FieldId fieldId) {
        this(UUID.randomUUID(), fieldId);
    }

    public UUID getFieldId() {
        return fieldId.getValue();
    }

    public int getVersion() {
        return fieldId.getVersion();
    }

    FieldId getField() {
        return fieldId;
    }
}

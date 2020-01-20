package com.pchudzik.edu.ddd.its.field;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@EqualsAndHashCode
public class FieldId {
    @Getter(AccessLevel.PACKAGE)
    private final UUID value;

    public FieldId() {
        this(UUID.randomUUID());
    }

    public FieldId(UUID value) {
        this.value = value;
    }
}

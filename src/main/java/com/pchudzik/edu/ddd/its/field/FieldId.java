package com.pchudzik.edu.ddd.its.field;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@EqualsAndHashCode
public class FieldId {
    @Getter
    private final UUID value;

    @Getter
    private final int version;

    public FieldId() {
        this(UUID.randomUUID(), 1);
    }

    public FieldId(UUID value, int version) {
        this.value = value;
        this.version = version;
    }

    public FieldId nextVersion() {
        return new FieldId(this.value, version + 1);
    }
}

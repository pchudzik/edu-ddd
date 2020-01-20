package com.pchudzik.edu.ddd.its.field;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class FieldVersion {
    private final int version;

    public FieldVersion next() {
        return new FieldVersion(version + 1);
    }
}

package com.pchudzik.edu.ddd.its.field;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class FieldName {
    @Getter
    private final String fieldName;

    @Getter
    private final String fieldDescription;

    public FieldName(String fieldName) {
        this.fieldName = fieldName;
        this.fieldDescription = null;
    }
}

package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.FieldType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public interface FieldReadFacade {
    List<FieldDto> listAllFields();

    @Getter
    @Builder(access = AccessLevel.PACKAGE)
    class FieldDto {
        private final FieldType type;
        private final FieldId id;
        private final String name;
        private final String description;
        private final Map<String, Object> configuration;
    }
}

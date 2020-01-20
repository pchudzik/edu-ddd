package com.pchudzik.edu.ddd.its.field;

import lombok.Builder;
import lombok.Getter;

public interface FieldCreationFacade {
    FieldId createStringField(StringFieldCreationCommand command);

    @Builder
    @Getter
    class StringFieldCreationCommand {
        private final String fieldName;
        private final String fieldDescription;
        private final boolean required;
        private final int minLength;
        private final int maxLength;
    }
}

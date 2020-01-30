package com.pchudzik.edu.ddd.its.field;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

public interface FieldCreation {
    FieldId createStringField(StringFieldCreationCommand command);

    FieldId createLabelField(LabelFieldCreationCommand fieldCreationCommand);

    @Builder
    @Getter
    class StringFieldCreationCommand {
        private final String fieldName;
        private final String fieldDescription;
        private final boolean required;
        private final int minLength;
        private final int maxLength;
    }

    @Getter
    @Builder
    class LabelFieldCreationCommand {
        private final String fieldName;
        private final String fieldDescription;
        private final boolean required;
        @Singular
        private final List<String> allowedValues;
    }
}

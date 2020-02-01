package com.pchudzik.edu.ddd.its.field;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

public interface FieldCreation {
    FieldId createStringField(StringFieldCreationCommand command);
    FieldId updateStringField(FieldId fieldId, StringFieldConfigurationUpdateCommand cmd);

    FieldId createLabelField(LabelFieldCreationCommand fieldCreationCommand);
    FieldId updateLabelField(FieldId fieldId, LabelFieldConfigurationUpdateCommand labelFieldConfigurationUpdateCommand);

    @Getter
    @Builder
    class StringFieldCreationCommand {
        private final String fieldName;
        private final String fieldDescription;
        private final boolean required;
        private final Integer minLength;
        private final Integer maxLength;
    }

    @Getter
    @Builder
    class StringFieldConfigurationUpdateCommand {
        private final boolean required;
        private final Integer minLength;
        private final Integer maxLength;
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

    @Getter
    @Builder
    class LabelFieldConfigurationUpdateCommand {
        private final boolean required;

        @Singular
        private final List<String> allowedLabels;
    }
}

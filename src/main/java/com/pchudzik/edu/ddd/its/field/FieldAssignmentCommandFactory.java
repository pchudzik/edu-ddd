package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.field.FieldAssignment.FieldAssignmentCommand;

import java.util.Collection;
import java.util.stream.Collectors;

public class FieldAssignmentCommandFactory {
    public static FieldAssignmentCommand<?> buildAssignmentCommand(FieldValueAssignmentCommand assignment) {
        switch (assignment.getFieldType()) {
            case STRING_FIELD:
                return new FieldAssignment.StringFieldAssignmentCommand(
                        assignment.getFieldId(),
                        (String) assignment.getValue());
            case LABEL_FIELD:
                return new FieldAssignment.LabelFieldAssignmentCommand(
                        assignment.getFieldId(),
                        convertRawValues((Collection<String>) assignment.getValue()));
            default:
                throw new IllegalArgumentException("Unsupported field type " + assignment.getFieldType());
        }
    }


    private static LabelValues convertRawValues(Collection<String> values) {
        return LabelValues.of(values.stream().map(LabelValues.LabelValue::of).collect(Collectors.toList()));
    }
}

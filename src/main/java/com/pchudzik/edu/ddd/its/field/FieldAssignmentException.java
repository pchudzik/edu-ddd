package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainException;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class FieldAssignmentException extends DomainException {
    public FieldAssignmentException() {
    }

    public FieldAssignmentException(String message) {
        super(message);
    }
}

class RequiredFieldsMissingException extends FieldAssignmentException {
    @Getter
    private final List<FieldId> missingRequiredFields;

    public RequiredFieldsMissingException(List<FieldId> missingRequiredFields) {
        super("" +
                "Required fields " +
                missingRequiredFields.stream().map(FieldId::toString).collect(Collectors.joining(", ")) +
                " no provided");
        this.missingRequiredFields = missingRequiredFields;
    }
}

class NotAvailableFieldsException extends FieldAssignmentException {

}

class FieldValueException extends FieldAssignmentException {

}

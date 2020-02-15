package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainException;

import java.util.ArrayList;
import java.util.Collection;
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
    private final List<FieldId> missingRequiredFields;

    public RequiredFieldsMissingException(Collection<FieldId> missingRequiredFields) {
        super("" +
                "Required fields not provided: " +
                missingRequiredFields.stream().map(FieldId::toString).collect(Collectors.joining(", ")));
        this.missingRequiredFields = new ArrayList<>(missingRequiredFields);
    }
}

class NotAvailableFieldsException extends FieldAssignmentException {
    private final ArrayList<FieldId> notAvailableFields;

    public NotAvailableFieldsException(Collection<FieldId> notAvailableFields) {
        super("" +
                "Not available fields provided for assignment: " +
                notAvailableFields.stream().map(FieldId::toString).collect(Collectors.joining(", ")));
        this.notAvailableFields = new ArrayList<>(notAvailableFields);
    }
}

class FieldValueException extends FieldAssignmentException {

}

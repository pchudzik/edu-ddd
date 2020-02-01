package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldAssignmentImpl implements FieldAssignment {
    private final TransactionManager txManager;

    private final StringFieldRepository stringFieldRepository;
    private final LabelFieldRepository labelFieldRepository;
    private final FieldValueRepository fieldValueRepository;

    public void assignStringToIssue(StringFieldAssignmentCommand assignmentCommand) {
        txManager.useTransaction(() -> {
            StringField field = stringFieldRepository.findStringField(assignmentCommand.getFieldId());
            FieldValue<String> value = field
                    .value(assignmentCommand.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.removeOldValues(assignmentCommand.getFieldId(), assignmentCommand.getIssueId());
            fieldValueRepository.saveStringValue(assignmentCommand.getIssueId(), value);
        });
    }

    @Override
    public void assignLabelFieldToIssue(LabelFieldAssignmentCommand assignmentCommand) {
        txManager.useTransaction(() -> {
            LabelField field = labelFieldRepository.findLabelField(assignmentCommand.getFieldId());
            FieldValue<LabelValues> value = field
                    .value(assignmentCommand.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.removeOldValues(assignmentCommand.getFieldId(), assignmentCommand.getIssueId());
            fieldValueRepository.saveLabelValue(assignmentCommand.getIssueId(), value);
        });
    }
}

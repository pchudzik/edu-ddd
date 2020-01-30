package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldAssignmentImpl implements FieldAssignment {

    private final TransactionManager txManager;

    private final FieldRepositoryImpl fieldRepository;

    private final FieldValueRepository fieldValueRepository;

    public <T> void assignFieldToIssue(FieldAssignmentCommand<T> assignmentCommand) {
        txManager.useTransaction(() -> {
            Field<T> field = fieldRepository.findOne(assignmentCommand.getFieldId());
            FieldValue<T> value = field
                    .value(assignmentCommand.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.save(assignmentCommand.getIssueId(), value);
        });
    }
}

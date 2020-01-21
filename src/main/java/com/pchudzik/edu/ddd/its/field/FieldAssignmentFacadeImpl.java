package com.pchudzik.edu.ddd.its.field;

import javax.inject.Inject;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldAssignmentFacadeImpl implements FieldAssignmentFacade {

    private final TransactionManager txManager;

    private final FieldRepository fieldRepository;

    private final FieldValueRepository fieldValueRepository;

    public <T> void assignFieldToIssue(FieldAssignmentCommand<T> assignmentCommand) {
        txManager.useTransaction(() -> {
            CustomField<T> field = fieldRepository.findOne(assignmentCommand.getFieldId());
            FieldValue<IssueId, T> value = field
                    .value(assignmentCommand.getIssueId(), assignmentCommand.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.save(assignmentCommand.getIssueId(), value);
        });
    }
}

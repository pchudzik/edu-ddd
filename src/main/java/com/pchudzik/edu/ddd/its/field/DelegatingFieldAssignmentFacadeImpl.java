package com.pchudzik.edu.ddd.its.field;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DelegatingFieldAssignmentFacadeImpl<T> implements FieldAssignmentFacade<T> {
    private final FieldRepository<T> fieldRepository;
    private final FieldValueRepository<T> fieldValueRepository;

    @Override
    public void assignFieldToIssue(FieldAssignmentCommand<T> assignmentCommand) {
        CustomField<T> field = fieldRepository.findOne(assignmentCommand.getFieldId());
        FieldValue<T> value = field
                .value(assignmentCommand.getValue())
                .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
        fieldValueRepository.save(assignmentCommand.getIssueId(), value);
    }
}

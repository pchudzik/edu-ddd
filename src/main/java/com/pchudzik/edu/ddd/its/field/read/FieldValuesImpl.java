package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldValuesImpl implements FieldValues {
    private final TransactionManager txManager;
    private final FieldValuesReadRepository fieldValueRepository;

    @Override
    public List<FieldValueDto<?, IssueId>> findFieldsAssignedToIssue(IssueId issueId) {
        return txManager.inTransaction(() -> fieldValueRepository
                .findValuesAssignedToIssue(issueId)).stream()
                .map(v -> (FieldValueDto<?, IssueId>)v)
                .collect(Collectors.toList());
    }

    @Override
    public List<FieldValueDto<?, ProjectId>> findFieldsAssignedToProject(ProjectId projectId) {
        return txManager.inTransaction(() -> fieldValueRepository
                .findValuesAssignedToProject(projectId)
                .stream()
                .map(v -> (FieldValueDto<?, ProjectId>) v)
                .collect(Collectors.toList()));
    }
}

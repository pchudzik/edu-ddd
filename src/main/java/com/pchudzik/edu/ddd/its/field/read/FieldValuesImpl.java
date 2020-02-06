package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldValuesImpl implements FieldValues {
    private final TransactionManager txManager;
    private final FieldValuesReadRepository fieldValueRepository;

    @Override
    public List<FieldValueDto<?, IssueId>> findFieldsAssignedToIssue(IssueId issueId) {
        return txManager.inTransaction(() -> fieldValueRepository.findValues(issueId));
    }

    @Override
    public List<FieldValueDto<?, ProjectId>> findFieldsAssignedToProject(ProjectId projectId) {
        return txManager.inTransaction(() -> fieldValueRepository.findValues(projectId));
    }
}

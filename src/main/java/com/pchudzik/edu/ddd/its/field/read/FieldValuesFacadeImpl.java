package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldValuesFacadeImpl implements FieldValuesFacade {
    private final TransactionManager txManager;
    private final FieldValuesReadRepository fieldValueRepository;

    @Override
    public List<FieldValueDto> findFieldsAssignedToIssue(IssueId issueId) {
        return txManager.inTransaction(() -> fieldValueRepository.findValues(issueId));
    }
}

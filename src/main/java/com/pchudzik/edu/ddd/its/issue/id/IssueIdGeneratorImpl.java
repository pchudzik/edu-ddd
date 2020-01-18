package com.pchudzik.edu.ddd.its.issue.id;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class IssueIdGeneratorImpl implements IssueIdGenerator {
    private final TransactionManager txManager;
    private final IssueIdGeneratorRepository repository;

    @Override
    public IssueId next(ProjectId projectId) {
        return txManager.inTransaction(() -> new IssueId(projectId, repository.nextId(projectId)));
    }
}

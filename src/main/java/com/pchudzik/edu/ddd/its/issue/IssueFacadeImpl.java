package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.issue.id.IssueIdGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class IssueFacadeImpl implements IssueFacade {
    private final TransactionManager txManager;
    private final IssueRepository issueRepository;
    private final IssueIdGenerator idGenerator;

    @Override
    public IssueId createIssue(IssueCreationCommand cmd) {
        return txManager.inTransaction(() -> {
            IssueId issueId = idGenerator.next(cmd.getProjectId());
            issueRepository.saveIssue(issueId, cmd.getTitle());
            return issueId;
        });
    }
}

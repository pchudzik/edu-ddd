package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.domain.ApplicationService;
import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainService;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.user.access.Access;

import javax.inject.Inject;

@ApplicationService
class IssueCreationAppService implements IssueCreation {
    private final Access access;
    private final TransactionManager txManager;
    private final IssueCreation issueCreation;

    @Inject
    IssueCreationAppService(Access access, TransactionManager txManager, @DomainService IssueCreation issueCreation) {
        this.access = access;
        this.txManager = txManager;
        this.issueCreation = issueCreation;
    }

    @Override
    public IssueId createIssue(IssueCreationCommand cmd) {
        return txManager.inTransaction(() ->
                access.ifCanCreateIssue(
                        cmd.getPrincipal(),
                        cmd.getProjectId(),
                        () -> issueCreation.createIssue(cmd)));
    }

    @Override
    public void updateIssue(IssueId issueId, IssueUpdateCommand cmd) {
        txManager.useTransaction(() ->
                access.ifCanUpdateIssue(
                        cmd.getPrincipal(),
                        issueId.getProject(),
                        () -> issueCreation.updateIssue(issueId, cmd)));
    }
}

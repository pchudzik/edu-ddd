package com.pchudzik.edu.ddd.its.issue.read;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.domain.NoSuchObjectException;
import com.pchudzik.edu.ddd.its.user.access.Access;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class IssueReadImpl implements IssueRead {
    private final IssueReadRepository readRepository;
    private final TransactionManager txManager;
    private final Access access;

    @Override
    public IssueDto findIssue(IssueLookupCommand cmd) {
        return txManager.inTransaction(() -> access.ifCanViewIssue(
                cmd.getPrincipal(),
                cmd.getIssueId().getProject(),
                () -> readRepository
                        .findOne(cmd.getIssueId())
                        .orElseThrow(() -> new NoSuchObjectException(cmd.getIssueId()))));
    }
}

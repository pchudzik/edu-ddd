package com.pchudzik.edu.ddd.its.issue.read;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.issue.read.IssueRead.IssueDto;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class IssueReadRepositoryImpl implements IssueReadRepository {
    private final Jdbi jdbi;

    @Override
    public Optional<IssueDto> findOne(IssueId issueId) {
        return jdbi.withHandle(h -> h
                .select("" +
                        "select project, issue_sequence, title from issue " +
                        "where project=:project and issue_sequence=:id")
                .bind("project", issueId.getProject().getValue())
                .bind("id", issueId.getIssue())
                .map(r -> new IssueDto(
                        new IssueId(
                                new ProjectId(r.getColumn("project", String.class)),
                                r.getColumn("issue_sequence", Integer.class)),
                        r.getColumn("title", String.class)))
                .findOne());
    }
}

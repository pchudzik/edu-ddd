package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class IssueRepository {
    private final Jdbi jdbi;

    public void saveIssue(IssueId issueId, String title) {
        jdbi.withHandle(h -> h
                .createUpdate("" +
                        "insert into issue(project, issue_sequence, title) " +
                        "values(:project, :id, :title)")
                .bind("project", issueId.getProject().getValue())
                .bind("id", issueId.getIssue())
                .bind("title", title)
                .execute());
    }
}

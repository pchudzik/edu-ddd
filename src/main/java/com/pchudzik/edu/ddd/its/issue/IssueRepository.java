package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class IssueRepository {
    private final Jdbi jdbi;

    public void saveIssue(Issue issue) {
        jdbi.useHandle(h -> {
            var updated = h
                    .createUpdate("" +
                            "update issue " +
                            "set title = :title " +
                            "where " +
                            "    issue_sequence = :id " +
                            "    and project = :project")
                    .bind("id", issue.getId().getIssue())
                    .bind("project", issue.getId().getProject().getValue())
                    .bind("title", issue.getTitle())
                    .execute();

            if (updated == 0) {
                h
                        .createUpdate("" +
                                "insert into issue(project, issue_sequence, title) " +
                                "values(:project, :id, :title)")
                        .bind("project", issue.getId().getProject().getValue())
                        .bind("id", issue.getId().getIssue())
                        .bind("title", issue.getTitle())
                        .execute();
            }
        });
    }

    public Issue findIssue(IssueId issueId) {
        return jdbi.withHandle(h -> h
                .select("select issue_sequence, project, title " +
                        "from issue " +
                        "where issue_sequence = :id and project = :project")
                .bind("id", issueId.getIssue())
                .bind("project", issueId.getProject().getValue())
                .map((rs, ctx) -> new Issue(
                        new IssueId(new ProjectId(rs.getString("project")), rs.getInt("issue_sequence")),
                        rs.getString("title")))
                .one());
    }
}

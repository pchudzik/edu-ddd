package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Builder;
import lombok.Getter;

public interface IssueFacade {
    IssueId createIssue(IssueCreationCommand cmd);

    @Builder
    @Getter
    class IssueCreationCommand {
        private final ProjectId projectId;
        private final String title;
    }
}

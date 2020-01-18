package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Builder;

public interface IssueFacade {
    void createIssue(IssueCreationCommand cmd);

    @Builder
    class IssueCreationCommand {
        private final ProjectId projectId;
        private final String title;
    }
}

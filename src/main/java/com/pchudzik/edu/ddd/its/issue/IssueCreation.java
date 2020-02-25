package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.field.FieldValueAssignmentCommand;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.access.AuthorizedCommand;
import com.pchudzik.edu.ddd.its.user.access.Principal;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

public interface IssueCreation {
    IssueId createIssue(IssueCreationCommand cmd);

    void updateIssue(IssueId issueId, IssueUpdateCommand issueUpdateCommand);

    @Builder
    @Getter
    class IssueCreationCommand implements AuthorizedCommand {
        private final ProjectId projectId;
        private final String title;
        @Singular
        private final List<FieldValueAssignmentCommand> fieldAssignments;
        private final Principal principal;
    }

    @Getter
    @Builder
    class IssueUpdateCommand implements AuthorizedCommand {
        private final String title;
        @Singular
        private final List<FieldValueAssignmentCommand> fieldAssignments;
        private final Principal principal;
    }
}

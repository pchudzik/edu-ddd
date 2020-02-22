package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.field.FieldValueAssignmentCommand;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.access.Access;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

public interface IssueCreation {
    IssueId createIssue(IssueCreationCommand cmd);

    void updateIssue(IssueId issueId, IssueUpdateCommand issueUpdateCommand);

    @Builder
    @Getter
    class IssueCreationCommand {
        private final ProjectId projectId;
        private final String title;
        @Singular
        private final List<FieldValueAssignmentCommand> fieldAssignments;

        public Access.Principal getPrincipal() {
            return null;
        }
    }

    @Getter
    @Builder
    class IssueUpdateCommand {
        private final String title;
        @Singular
        private final List<FieldValueAssignmentCommand> fieldAssignments;

        public Access.Principal getPrincipal() {
            return null;
        }
    }
}

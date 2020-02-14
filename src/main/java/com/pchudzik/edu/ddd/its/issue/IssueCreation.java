package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.FieldType;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;

public interface IssueCreation {
    IssueId createIssue(IssueCreationCommand cmd);

    @Builder
    @Getter
    class IssueCreationCommand {
        private final ProjectId projectId;
        private final String title;
        @Singular
        private final List<FieldToIssueAssignmentCommand> fieldAssignments;
    }

    @Getter
    @RequiredArgsConstructor
    class FieldToIssueAssignmentCommand<V> {
        private final FieldId fieldId;
        private final V value;
        private final FieldType fieldType;
    }
}

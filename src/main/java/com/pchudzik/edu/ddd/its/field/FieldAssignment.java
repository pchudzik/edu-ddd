package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.Builder;
import lombok.Getter;

public interface FieldAssignment {
    <T> void assignFieldToIssue(FieldAssignmentCommand<T> assignmentCommand);

    @Builder
    @Getter
    class FieldAssignmentCommand<T> {
        private final FieldId fieldId;
        private final IssueId issueId;
        private final T value;
    }
}

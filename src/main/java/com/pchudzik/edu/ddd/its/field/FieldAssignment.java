package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.Builder;
import lombok.Getter;

public interface FieldAssignment {
    void assignStringToIssue(StringFieldAssignmentCommand assignmentCommand);

    void assignLabelFieldToIssue(LabelFieldAssignmentCommand assignmentCommand);

    @Getter
    @Builder
    class StringFieldAssignmentCommand {
        private final FieldId fieldId;
        private final IssueId issueId;
        private final String value;
    }

    @Getter
    @Builder
    class LabelFieldAssignmentCommand {
        private final FieldId fieldId;
        private final IssueId issueId;
        private final LabelValues value;
    }
}

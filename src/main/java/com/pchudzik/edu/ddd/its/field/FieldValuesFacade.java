package com.pchudzik.edu.ddd.its.field;

import java.util.List;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.Builder;
import lombok.Getter;

public interface FieldValuesFacade<T> {
    List<FieldValueDto<T>> findFieldsAssignedToIssue(IssueId issueId);

    @Builder
    @Getter
    class FieldValueDto<T> {
        private final FieldId fieldId;
        private final FieldVersion fieldVersion;
        private final IssueId issueId;
        private final T value;
    }
}

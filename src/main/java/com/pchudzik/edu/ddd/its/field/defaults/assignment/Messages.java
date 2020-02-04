package com.pchudzik.edu.ddd.its.field.defaults.assignment;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Messages {
    @Getter
    @RequiredArgsConstructor
    public static class FieldAssignmentRemovedFromProject implements MessageQueue.Message {
        private final FieldId fieldId;
        private final ProjectId projectId;
    }

    @Getter
    @RequiredArgsConstructor
    public static class FieldAssignmentRemovedFromIssue implements MessageQueue.Message {
        private final FieldId fieldId;
        private final IssueId issueId;
    }
}

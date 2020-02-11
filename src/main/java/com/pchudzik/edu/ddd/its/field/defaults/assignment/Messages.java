package com.pchudzik.edu.ddd.its.field.defaults.assignment;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

public class Messages {
    @RequiredArgsConstructor
    public static class FieldAssignmentRemovedFromProject implements MessageQueue.Message {
        @Getter
        private final FieldId fieldId;
        private final ProjectId projectId;

        public FieldAssignmentRemovedFromProject(FieldId fieldId) {
            this(fieldId, null);
        }

        public Optional<ProjectId> getProjectId() {
            return Optional.ofNullable(projectId);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class FieldAssignmentRemovedFromIssue implements MessageQueue.Message {
        private final FieldId fieldId;
        private final IssueId issueId;
    }
}

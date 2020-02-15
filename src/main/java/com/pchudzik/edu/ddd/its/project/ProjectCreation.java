package com.pchudzik.edu.ddd.its.project;

import com.pchudzik.edu.ddd.its.field.FieldValueAssignmentCommand;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import lombok.*;

import java.util.List;

public interface ProjectCreation {
    ProjectId createNewProject(ProjectCreationCommand creationCommand);

    @Data
    @Builder
    class ProjectCreationCommand {
        private final ProjectId id;
        private final String name;
        private final String description;
        @Singular
        private final List<FieldValueAssignmentCommand> fieldAssignments;
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    class ProjectCreatedMessage implements MessageQueue.Message {
        @Getter
        private final ProjectId projectId;
    }
}

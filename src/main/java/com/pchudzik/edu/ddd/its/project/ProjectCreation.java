package com.pchudzik.edu.ddd.its.project;

import com.pchudzik.edu.ddd.its.field.FieldValueAssignmentCommand;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import com.pchudzik.edu.ddd.its.user.access.AuthorizedCommand;
import com.pchudzik.edu.ddd.its.user.access.Principal;
import lombok.*;

import java.util.List;

public interface ProjectCreation {
    ProjectId createNewProject(ProjectCreationCommand creationCommand);

    void updateProject(ProjectId projectId, ProjectUpdateCommand updateCommand);

    @Getter
    @Builder
    class ProjectCreationCommand implements AuthorizedCommand {
        private final ProjectId id;
        private final String name;
        private final String description;
        private final Principal principal;
        @Singular
        private final List<FieldValueAssignmentCommand> fieldAssignments;
    }

    @Getter
    @Builder
    class ProjectUpdateCommand implements AuthorizedCommand{
        private final String name;
        private final String description;
        private final Principal principal;
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

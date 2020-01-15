package com.pchudzik.edu.ddd.its.project;

import lombok.Builder;
import lombok.Data;

public interface ProjectFacade {
    void createNewProject(ProjectCreationCommand creationCommand);

    @Data
    @Builder
    class ProjectCreationCommand {
        private final String id;
        private final String name;
        private final String description;
    }
}

package com.pchudzik.edu.ddd.its.project.read;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public interface ProjectViewFacade {
    List<ProjectDto> listProjects();

    @Data
    @Builder
    @EqualsAndHashCode
    class ProjectDto {
        private final String id;
        private final String name;
        private final String description;
    }
}

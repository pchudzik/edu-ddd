package com.pchudzik.edu.ddd.its.project.read;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public interface ProjectView {
    List<ProjectDto> listProjects();

    @Data
    @Builder
    @EqualsAndHashCode
    class ProjectDto {
        private final ProjectId id;
        private final String name;
        private final String description;
    }
}

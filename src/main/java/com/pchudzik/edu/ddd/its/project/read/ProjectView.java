package com.pchudzik.edu.ddd.its.project.read;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.*;

import java.util.List;

import static com.pchudzik.edu.ddd.its.user.access.Access.Principal;

public interface ProjectView {
    List<ProjectDto> listProjects(ListProjectsCmd cmd);

    @Data
    @Builder
    @EqualsAndHashCode
    class ProjectDto {
        private final ProjectId id;
        private final String name;
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    class ListProjectsCmd {
        private final Principal principal;
    }
}

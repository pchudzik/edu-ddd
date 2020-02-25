package com.pchudzik.edu.ddd.its.project.read;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.access.AuthorizedCommand;
import com.pchudzik.edu.ddd.its.user.access.Principal;
import lombok.*;

import java.util.List;

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
    class ListProjectsCmd implements AuthorizedCommand {
        private final Principal principal;
    }
}

package com.pchudzik.edu.ddd.its.project.read;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class ProjectViewRepositoryImpl implements ProjectViewRepository {
    private final Jdbi jdbi;

    @Override
    public List<ProjectView.ProjectDto> listProjects() {
        return jdbi.withHandle(h -> h
                .createQuery("select id, name, description from project")
                .map((rs, ctx) -> ProjectView.ProjectDto.builder()
                        .id(new ProjectId(rs.getNString("id")))
                        .name(rs.getNString("name"))
                        .description(rs.getString("description"))
                        .build())
                .list());
    }
}

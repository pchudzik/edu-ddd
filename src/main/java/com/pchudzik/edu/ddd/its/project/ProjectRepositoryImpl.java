package com.pchudzik.edu.ddd.its.project;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class ProjectRepositoryImpl implements ProjectRepository {
    private final Jdbi jdbi;

    @Override
    public void save(Project project) {
        jdbi.useHandle(h -> {
            var updated = h.createUpdate("" +
                    "update project " +
                    "set " +
                    "    name = :name, " +
                    "    description = :description " +
                    "where id = :id")
                    .bind("id", project.getId().getValue())
                    .bind("name", project.getProjectFullName())
                    .bind("description", project.getProjectDescription())
                    .execute();

            if (updated == 0) {
                h
                        .createUpdate("" +
                                "insert into project(id, name, description)" +
                                "values(:id, :name, :description)")
                        .bind("id", project.getId().getValue())
                        .bind("name", project.getProjectFullName())
                        .bind("description", project.getProjectDescription())
                        .execute();
            }
        });
    }

    @Override
    public Project findOne(ProjectId projectId) {
        return jdbi.withHandle(h -> h
                .select("" +
                        "select id, name, description " +
                        "from project " +
                        "where id = :id")
                .bind("id", projectId.getValue())
                .map((rs, ctx) -> new Project(
                        new ProjectId(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("description")))
                .one());
    }
}

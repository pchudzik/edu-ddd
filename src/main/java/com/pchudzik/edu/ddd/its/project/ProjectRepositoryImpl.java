package com.pchudzik.edu.ddd.its.project;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class ProjectRepositoryImpl implements ProjectRepository {
    private final Jdbi jdbi;

    @Override
    public void save(Project project) {
        jdbi.withHandle(h -> h
                .createUpdate("" +
                        "insert into project(id, name, description)" +
                        "values(:id, :name, :description)")
                .bind("id", project.getId().getValue())
                .bind("name", project.getProjectFullName())
                .bind("description", project.getProjectDescription())
                .execute());
    }
}

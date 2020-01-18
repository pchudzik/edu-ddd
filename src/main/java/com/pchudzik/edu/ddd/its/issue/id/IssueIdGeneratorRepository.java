package com.pchudzik.edu.ddd.its.issue.id;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class IssueIdGeneratorRepository {
    private final Jdbi jdbi;

    public void createGenerator(ProjectId projectId) {
        jdbi.withHandle(h -> h
                .createUpdate("insert into issue_id_generator(project, sequence) values(:project, :value)")
                .bind("project", projectId.getValue())
                .bind("value", 1)
                .execute());
    }

    public int nextId(ProjectId projectId) {
        Integer value = jdbi.withHandle(h -> h
                .select("select sequence from issue_id_generator where project=:project")
                .bind("project", projectId.getValue())
                .mapTo(Integer.class))
                .one();
        jdbi.withHandle(h -> h
                .createUpdate("" +
                        "update issue_id_generator " +
                        "set sequence = sequence + 1 " +
                        "where project=:project and sequence=:current")
                .bind("project", projectId.getValue())
                .bind("current", value)
                .execute());
        return value;
    }
}

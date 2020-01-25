package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldValueRepository {
    private final Jdbi jdbi;

    public <T> void save(IssueId issueId, FieldValue<T> value) {
        jdbi.withHandle(h -> h
                .createUpdate("" +
                        "insert into field_value(id, field_id, project, issue, value) " +
                        "values(:id, :fieldId, :project, :issue, :value)")
                .bind("id", UUID.randomUUID().toString())
                .bind("fieldId", value.getFieldId().getValue())
                .bind("project", issueId.getProject().getValue())
                .bind("issue", issueId.getIssue())
                .bind("value", value.getValue())
                .execute());
    }

    public List<FieldValuesFacade.FieldValueDto> findValues(IssueId issueId) {
        return jdbi.withHandle(h -> h
                .select("" +
                        "select " +
                        "  value.field_id fieldId, " +
                        "  last_field.version version, " +
                        "  value.project project, " +
                        "  value.issue issue, " +
                        "  value.value value, " +
                        "  field.type type " +
                        "from field_value value " +
                        "join last_field last_field on last_field.id = value.field_id " +
                        "join field field on field.id = value.field_id " +
                        "where " +
                        "  project=:project " +
                        "  and issue=:issue")
                .bind("project", issueId.getProject().getValue())
                .bind("issue", issueId.getIssue())
                .map((rs, ctx) -> new FieldValuesFacade.FieldValueDto(
                        new FieldId(
                                UUID.fromString(rs.getString("fieldId")),
                                rs.getInt("version")),
                        new IssueId(
                                new ProjectId(rs.getString("project")),
                                rs.getInt("issue")),
                        FieldType.valueOf(rs.getString("type")),
                        new FieldValuesFacade.Value(rs.getString("value"))))
                .list());
    }
}

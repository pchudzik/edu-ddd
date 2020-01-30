package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.FieldType;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldValuesReadRepository {
    private final Jdbi jdbi;

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

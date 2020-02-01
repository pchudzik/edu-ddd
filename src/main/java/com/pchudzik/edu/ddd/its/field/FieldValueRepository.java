package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldValueRepository {
    private final Jdbi jdbi;

    public void saveStringValue(IssueId issueId, FieldValue<String> value) {
        jdbi.withHandle(h -> h
                .createUpdate("" +
                        "insert into field_value(id, field_id, field_version, project, issue, value) " +
                        "values(:id, :fieldId, :fieldVersion, :project, :issue, :value)")
                .bind("id", UUID.randomUUID().toString())
                .bind("fieldId", value.getFieldId().getValue())
                .bind("fieldVersion", value.getFieldId().getVersion())
                .bind("project", issueId.getProject().getValue())
                .bind("issue", issueId.getIssue())
                .bind("value", value.getValue())
                .execute());
    }

    public void saveLabelValue(IssueId issueId, FieldValue<LabelValues> value) {
        jdbi.useHandle(handle -> {
            var batch = handle
                    .prepareBatch("" +
                            "insert into field_value(id, field_id, field_version, project, issue, value) " +
                            "values(:id, :fieldId, :fieldVersion, :project, :issue, :value)");
            value.getValue().forEach(l -> batch
                    .bind("id", UUID.randomUUID())
                    .bind("fieldId", value.getFieldId().getValue())
                    .bind("fieldVersion", value.getFieldId().getVersion())
                    .bind("project", issueId.getProject().getValue())
                    .bind("issue", issueId.getIssue())
                    .bind("value", l.getValue())
                    .add());
            batch.execute();
        });
    }

    public void removeOldValues(FieldId fieldId, IssueId issueId) {
        jdbi.withHandle(h -> h
                .createUpdate("" +
                        "delete from field_value where " +
                        "field_id = :fieldId " +
                        "and project = :project " +
                        "and issue = :issue")
                .bind("fieldId", fieldId.getValue())
                .bind("project", issueId.getProject().getValue())
                .bind("issue", issueId.getIssue())
                .execute());
    }
}

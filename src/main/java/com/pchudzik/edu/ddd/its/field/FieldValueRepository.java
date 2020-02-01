package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import javax.inject.Inject;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldValueRepository {
    private final Jdbi jdbi;

    public <T> void saveStringValue(IssueId issueId, FieldValue<T> value) {
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

    public void saveLabelValue(IssueId issueId, FieldValue<LabelValues> value) {
        jdbi.useHandle(handle -> {
            PreparedBatch batch = handle
                    .prepareBatch("" +
                            "insert into field_value(id, field_id, project, issue, value) " +
                            "values(:id, :fieldId, :project, :issue, :value)");
            value.getValue().forEach(l -> batch
                    .bind("id", UUID.randomUUID())
                    .bind("fieldId", value.getFieldId().getValue())
                    .bind("project", issueId.getProject().getValue())
                    .bind("issue", issueId.getIssue())
                    .bind("value", l.getValue())
                    .add());
            batch.execute();
        });
    }
}

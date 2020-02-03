package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldValueRepository {
    private final Jdbi jdbi;

    public FieldValueId saveStringValue(IssueId issueId, FieldValue<String> value) {
        return jdbi.withHandle(handle -> {
            var fieldValueId = new FieldValueId(
                    UUID.randomUUID(),
                    value.getFieldId());

            handle.createUpdate("" +
                    "insert into field_value(id, field_id, field_version, project, issue, value) " +
                    "values(:id, :fieldId, :fieldVersion, :project, :issue, :value)")
                    .bind("id", fieldValueId.getValueId())
                    .bind("fieldId", fieldValueId.getFieldId())
                    .bind("fieldVersion", fieldValueId.getVersion())
                    .bind("project", issueId.getProject().getValue())
                    .bind("issue", issueId.getIssue())
                    .bind("value", value.getValue())
                    .execute();

            return fieldValueId;
        });
    }

    public Collection<FieldValueId> saveLabelValue(IssueId issueId, FieldValue<LabelValues> value) {
        return jdbi.withHandle(handle -> {
            var batch = handle
                    .prepareBatch("" +
                            "insert into field_value(id, field_id, field_version, project, issue, value) " +
                            "values(:id, :fieldId, :fieldVersion, :project, :issue, :value)");
            var keysWithValues = value
                    .getValue().stream()
                    .collect(Collectors.toMap(
                            v -> new FieldValueId(UUID.randomUUID(), value.getFieldId()),
                            LabelValues.LabelValue::getValue));

            keysWithValues
                    .entrySet()
                    .forEach(entry -> batch
                            .bind("id", entry.getKey().getValueId())
                            .bind("fieldId", entry.getKey().getFieldId())
                            .bind("fieldVersion", entry.getKey().getVersion())
                            .bind("project", issueId.getProject().getValue())
                            .bind("issue", issueId.getIssue())
                            .bind("value", entry.getValue())
                            .add());
            batch.execute();

            return keysWithValues.keySet();
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

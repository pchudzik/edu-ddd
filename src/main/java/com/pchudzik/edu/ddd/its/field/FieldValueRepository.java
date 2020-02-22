package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class FieldValueRepository {
    private final Jdbi jdbi;

    public FieldValueId saveStringValue(ProjectId projectId, FieldValue<String> value) {
        return saveStringValue(value, projectId, null);
    }

    public FieldValueId saveStringValue(IssueId issueId, FieldValue<String> value) {
        return saveStringValue(value, issueId.getProject(), issueId);
    }

    private FieldValueId saveStringValue(FieldValue<String> value, ProjectId project, @Nullable IssueId issueId) {
        return jdbi.withHandle(handle -> {
            var fieldValueId = new FieldValueId(
                    UUID.randomUUID(),
                    value.getFieldId());
            var maybeIssue = Optional.ofNullable(issueId);
            var update = handle.createUpdate("" +
                    "insert into field_value(id, field_id, field_version, project, <issueColumn> value) " +
                    "values(:id, :fieldId, :fieldVersion, :project, <issueValue> :value)")
                    .define("issueColumn", maybeIssue.map(i -> "issue,").orElse(""))
                    .define("issueValue", maybeIssue.map(i -> " :issue,").orElse(""))
                    .bind("id", fieldValueId.getValueId())
                    .bind("fieldId", fieldValueId.getFieldId())
                    .bind("fieldVersion", fieldValueId.getVersion())
                    .bind("project", project.getValue())
                    .bind("value", value.getValue());

            maybeIssue.ifPresent(issue -> update.bind("issue", issue.getIssue()));

            update.execute();

            return fieldValueId;
        });
    }

    public Collection<FieldValueId> saveLabelValue(ProjectId projectId, FieldValue<LabelValues> value) {
        return saveLabelValue(projectId, null, value);
    }

    public Collection<FieldValueId> saveLabelValue(IssueId issueId, FieldValue<LabelValues> value) {
        return saveLabelValue(issueId.getProject(), issueId, value);
    }

    private Collection<FieldValueId> saveLabelValue(ProjectId projectId, @Nullable IssueId issueId, FieldValue<LabelValues> value) {
        return jdbi.withHandle(handle -> {
            var maybeIssue = Optional.ofNullable(issueId);
            var batch = handle
                    .prepareBatch("" +
                            "insert into field_value(id, field_id, field_version, project, <issueColumn> value) " +
                            "values(:id, :fieldId, :fieldVersion, :project, <issueValue> :value)");
            batch
                    .define("issueColumn", maybeIssue.map(i -> "issue, ").orElse(""))
                    .define("issueValue", maybeIssue.map(i -> ":issue, ").orElse(""));

            var keysWithValues = value
                    .getValue().stream()
                    .collect(Collectors.toMap(
                            v -> new FieldValueId(UUID.randomUUID(), value.getFieldId()),
                            LabelValues.LabelValue::getValue));

            keysWithValues
                    .entrySet()
                    .forEach(entry -> {
                        batch
                                .bind("id", entry.getKey().getValueId())
                                .bind("fieldId", entry.getKey().getFieldId())
                                .bind("fieldVersion", entry.getKey().getVersion())
                                .bind("project", projectId.getValue())
                                .bind("value", entry.getValue());
                        maybeIssue.ifPresent(i -> batch.bind("issue", issueId.getIssue()));
                        batch.add();
                    });
            batch.execute();

            return keysWithValues.keySet();
        });
    }


    public void removeOldValues(FieldId fieldId, ProjectId projectId) {
        removeOldValues(fieldId, projectId, null);
    }

    public void removeOldValues(FieldId fieldId, IssueId issueId) {
        removeOldValues(fieldId, issueId.getProject(), issueId);
    }

    private void removeOldValues(FieldId fieldId, ProjectId projectId, @Nullable IssueId issueId) {
        jdbi.useHandle(h -> {
            var update = h
                    .createUpdate("" +
                            "delete from field_value where " +
                            "field_id = :fieldId " +
                            "and project = :project " +
                            "and <issueSql>")
                    .define("issueSql", Optional.ofNullable(issueId)
                            .map(issue -> "issue = :issue")
                            .orElse("issue is null"))
                    .bind("fieldId", fieldId.getValue())
                    .bind("project", projectId.getValue());

            if (issueId != null) {
                update.bind("issue", issueId.getIssue());
            }

            update.execute();
        });
    }
}

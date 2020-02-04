package com.pchudzik.edu.ddd.its.field.defaults.assignment;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.read.AvailableFields;
import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldDefinitionsImpl implements FieldDefinitions {
    private final TransactionManager txManager;
    private final Jdbi jdbi;
    private final AvailableFields availableFields;
    private final MessageQueue messageQueue;

    @Override
    public void assignDefaultFields(ProjectId projectId, Collection<FieldId> fieldIds) {
        txManager.useTransaction(() -> saveAllFieldAssignments(fieldIds.stream()
                .map(f -> new DefaultFieldAssignment(projectId, f))
                .collect(Collectors.toList())));
    }

    @Override
    public void assignDefaultFields(IssueId issueId, Collection<FieldId> fieldIds) {
        txManager.useTransaction(() -> saveAllFieldAssignments(fieldIds.stream()
                .map(f -> new DefaultFieldAssignment(issueId, f))
                .collect(Collectors.toList())));
    }

    @Override
    public void removeDefaultFields(ProjectId projectId, FieldId fieldId) {
        txManager.useTransaction(() -> {
            jdbi.withHandle(h -> h
                    .createUpdate("" +
                            "delete from field_definitions " +
                            "where " +
                            "    field_id = :fieldId " +
                            "    and field_version = :fieldVersion " +
                            "    and project = :project " +
                            "    and issue is null")
                    .bind("fieldId", fieldId.getValue())
                    .bind("fieldVersion", fieldId.getVersion())
                    .bind("project", projectId.getValue())
                    .execute());
            messageQueue.publish(new Messages.FieldAssignmentRemovedFromProject(fieldId, projectId));
        });
    }

    @Override
    public void removeDefaultFields(IssueId issueId, FieldId fieldId) {
        txManager.useTransaction(() -> {
            jdbi.withHandle(h -> h
                    .createUpdate("" +
                            "delete from field_definitions " +
                            "where " +
                            "    field_id = :fieldId " +
                            "    and field_version = :fieldVersion " +
                            "    and project = :project " +
                            "    and issue = :issue")
                    .bind("fieldId", fieldId.getValue())
                    .bind("fieldVersion", fieldId.getVersion())
                    .bind("project", issueId.getProject().getValue())
                    .bind("issue", issueId.getIssue())
                    .execute());
            messageQueue.publish(new Messages.FieldAssignmentRemovedFromIssue(fieldId, issueId));
        });
    }

    @Override
    public Collection<AvailableFields.FieldDto> findDefaultFields(ProjectId projectId) {
        return txManager.inTransaction(() -> {
            List<FieldId> fieldsForProject = jdbi.withHandle(h -> h.select("" +
                    "select field_id, field_version " +
                    "from field_definitions " +
                    "where " +
                    "    project = :project " +
                    "    and issue is null")
                    .bind("project", projectId.getValue())
                    .map(new FieldIdRowMapper())
                    .list());
            return availableFields.findByIds(fieldsForProject);
        });
    }

    @Override
    public Collection<AvailableFields.FieldDto> findDefaultFields(IssueId issueId) {
        return txManager.inTransaction(() -> {
            List<FieldId> fieldsForProject = jdbi.withHandle(h -> h.select("" +
                    "select field_id, field_version " +
                    "from field_definitions " +
                    "where " +
                    "    project = :project " +
                    "    and issue = :issue")
                    .bind("project", issueId.getProject().getValue())
                    .bind("issue", issueId.getIssue())
                    .map(new FieldIdRowMapper())
                    .list());
            return availableFields.findByIds(fieldsForProject);
        });
    }

    private void saveAllFieldAssignments(Collection<DefaultFieldAssignment> assignments) {
        jdbi.useHandle(handle -> {
            var batch = handle.prepareBatch("" +
                    "insert into field_definitions(field_id, field_version, project, issue) " +
                    "values (:fieldId, :fieldVersion, :project, :issue)");
            assignments.forEach(a -> {
                batch
                        .bind("fieldId", a.getFieldId().getValue())
                        .bind("fieldVersion", a.getFieldId().getVersion())
                        .bind("project", a.getProjectId()
                                .orElseGet(() -> a.getIssueId().get().getProject())
                                .getValue());
                if (a.getIssueId().isPresent()) {
                    batch.bind("issue", a.getIssueId().get().getIssue());
                } else {
                    batch.bindNull("issue", JDBCType.INTEGER.getVendorTypeNumber());
                }
                batch.add();
            });
            batch.execute();
        });
    }

    @RequiredArgsConstructor
    private static class DefaultFieldAssignment {
        private final ProjectId projectId;
        private final IssueId issueId;

        @Getter
        private final FieldId fieldId;

        public DefaultFieldAssignment(ProjectId projectId, FieldId fieldId) {
            this(projectId, null, fieldId);
        }

        public DefaultFieldAssignment(@Nullable IssueId issueId, FieldId fieldId) {
            this(null, issueId, fieldId);
        }

        public Optional<ProjectId> getProjectId() {
            return Optional.ofNullable(projectId);
        }

        public Optional<IssueId> getIssueId() {
            return Optional.ofNullable(issueId);
        }
    }

    private static class FieldIdRowMapper implements RowMapper<FieldId> {
        @Override
        public FieldId map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new FieldId(
                    UUID.fromString(rs.getString("field_id")),
                    rs.getInt("field_version"));
        }
    }
}

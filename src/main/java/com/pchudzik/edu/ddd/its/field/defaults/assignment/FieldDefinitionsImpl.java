package com.pchudzik.edu.ddd.its.field.defaults.assignment;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.read.AvailableFields;
import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
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
    public void removeDefaultFields(ProjectId projectId, FieldId fieldId) {
        txManager.useTransaction(() -> {
            jdbi.withHandle(h -> h
                    .createUpdate("" +
                            "delete from field_definitions " +
                            "where " +
                            "    field_id = :fieldId " +
                            "    and field_version = :fieldVersion " +
                            "    and project = :project")
                    .bind("fieldId", fieldId.getValue())
                    .bind("fieldVersion", fieldId.getVersion())
                    .bind("project", projectId.getValue())
                    .execute());
            messageQueue.publish(new Messages.FieldAssignmentRemovedFromProject(fieldId, projectId));
        });
    }

    @Override
    public Collection<AvailableFields.FieldDto> findDefaultFields(ProjectId projectId) {
        return txManager.inTransaction(() -> {
            List<FieldId> fieldsForProject = jdbi.withHandle(h -> h.select("" +
                    "select field_id, field_version " +
                    "from field_definitions " +
                    "where project = :project")
                    .bind("project", projectId.getValue())
                    .map(new FieldIdRowMapper())
                    .list());
            return availableFields.findByIds(fieldsForProject);
        });
    }

    private void saveAllFieldAssignments(Collection<DefaultFieldAssignment> assignments) {
        jdbi.useHandle(handle -> {
            var batch = handle.prepareBatch("" +
                    "insert into field_definitions(field_id, field_version, project) " +
                    "values (:fieldId, :fieldVersion, :project)");
            assignments.forEach(a -> batch
                    .bind("fieldId", a.getFieldId().getValue())
                    .bind("fieldVersion", a.getFieldId().getVersion())
                    .bind("project", a.getProjectId().getValue())
                    .add());
            batch.execute();
        });
    }

    @Getter
    @RequiredArgsConstructor
    private static class DefaultFieldAssignment {
        private final ProjectId projectId;
        private final FieldId fieldId;
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

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
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldDefinitionsImpl implements FieldDefinitions {
    private static final String emptyProject = "_____";

    private final TransactionManager txManager;
    private final Jdbi jdbi;
    private final AvailableFields availableFields;
    private final MessageQueue messageQueue;

    @Override
    public void assignDefaultFields(Collection<FieldId> fieldIds) {
        txManager.useTransaction(() -> saveAllFieldAssignments(fieldIds.stream()
                .map(DefaultFieldAssignment::new)
                .collect(Collectors.toList())));
    }

    @Override
    public void assignDefaultFields(ProjectId projectId, Collection<FieldId> fieldIds) {
        txManager.useTransaction(() -> saveAllFieldAssignments(fieldIds.stream()
                .map(f -> new DefaultFieldAssignment(projectId, f))
                .collect(Collectors.toList())));
    }

    @Override
    public void removeDefaultFields(FieldId fieldId) {
        txManager.useTransaction(() -> {
            removeDefaultFields(emptyProject, fieldId);
            messageQueue.publish(new Messages.FieldAssignmentRemovedFromProject(fieldId));
        });
    }

    @Override
    public void removeDefaultFields(ProjectId projectId, FieldId fieldId) {
        txManager.useTransaction(() -> {
            removeDefaultFields(projectId.getValue(), fieldId);
            messageQueue.publish(new Messages.FieldAssignmentRemovedFromProject(fieldId, projectId));
        });
    }

    private void removeDefaultFields(String projectId, FieldId fieldId) {
        jdbi.withHandle(h -> h
                .createUpdate("" +
                        "delete from field_definitions " +
                        "where " +
                        "    field_id = :fieldId " +
                        "    and field_version = :fieldVersion " +
                        "    and project = :project")
                .bind("fieldId", fieldId.getValue())
                .bind("fieldVersion", fieldId.getVersion())
                .bind("project", projectId)
                .execute());
    }

    @Override
    public Collection<AvailableFields.FieldDto> findDefaultFields() {
        return findDefaultFields(emptyProject);
    }

    @Override
    public Collection<AvailableFields.FieldDto> findDefaultFields(ProjectId projectId) {
        return findDefaultFields(projectId.getValue());
    }

    private Collection<AvailableFields.FieldDto> findDefaultFields(String projectId) {
        return txManager.inTransaction(() -> availableFields.findByIds(findFieldIds(projectId)));
    }

    private Set<FieldId> findFieldIds(String projectId) {
        return new HashSet<>(jdbi.withHandle(h -> h.select("" +
                "select field_id, field_version " +
                "from field_definitions " +
                "where project = :project")
                .bind("project", projectId)
                .map(new FieldIdRowMapper())
                .list()));
    }

    @Override
    public boolean allRequiredFieldsProvided(Collection<FieldId> fields) {
        return allRequiredFieldsProvided(
                findFieldIds(emptyProject),
                fields);
    }

    @Override
    public boolean allRequiredFieldsProvided(ProjectId projectId, Collection<FieldId> fields) {
        return allRequiredFieldsProvided(
                findFieldIds(projectId.getValue()),
                fields);
    }

    private boolean allRequiredFieldsProvided(Collection<FieldId> requiredFields, Collection<FieldId> providedFields) {
        return providedFields.containsAll(requiredFields);
    }

    private void saveAllFieldAssignments(Collection<DefaultFieldAssignment> assignments) {
        jdbi.useHandle(handle -> {
            var batch = handle.prepareBatch("" +
                    "insert into field_definitions(field_id, field_version, project) " +
                    "values (:fieldId, :fieldVersion, :project)");
            assignments.forEach(a -> batch
                    .bind("fieldId", a.getFieldId().getValue())
                    .bind("fieldVersion", a.getFieldId().getVersion())
                    .bind("project", a.getProjectId().map(ProjectId::getValue).orElse(emptyProject))
                    .add());
            batch.execute();
        });
    }

    @RequiredArgsConstructor
    private static class DefaultFieldAssignment {
        private final ProjectId projectId;

        @Getter
        private final FieldId fieldId;

        public DefaultFieldAssignment(FieldId fieldId) {
            this(null, fieldId);
        }

        public Optional<ProjectId> getProjectId() {
            return Optional.ofNullable(projectId);
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

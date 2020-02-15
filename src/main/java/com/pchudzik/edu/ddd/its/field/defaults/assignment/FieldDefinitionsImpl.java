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
                        "    and project = :project")
                .bind("fieldId", fieldId.getValue())
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

    @Override
    public AssignmentValidator checkAssignments(Collection<FieldId> toAssignFieldIds) {
        var availableFields = findDefaultFields();
        return new FieldsAssignmentValidator(availableFields, toAssignFieldIds);
    }

    @Override
    public AssignmentValidator checkAssignments(ProjectId projectId, Collection<FieldId> toAssignFieldIds) {
        var availableFields = findDefaultFields(projectId);
        return new FieldsAssignmentValidator(availableFields, toAssignFieldIds);
    }

    private Collection<AvailableFields.FieldDto> findDefaultFields(String projectId) {
        return txManager.inTransaction(() -> availableFields.findByIds(findFieldIds(projectId)));
    }

    private Set<FieldId> findFieldIds(String projectId) {
        return new HashSet<>(jdbi.withHandle(h -> h.select("" +
                "select " +
                "    definitions.field_id field_id, " +
                "    last_field.version field_version " +
                "from field_definitions definitions " +
                "join last_field on definitions.field_id = last_field.id " +
                "where project = :project")
                .bind("project", projectId)
                .map(new FieldIdRowMapper())
                .list()));
    }

    private void saveAllFieldAssignments(Collection<DefaultFieldAssignment> assignments) {
        jdbi.useHandle(handle -> {
            var batch = handle.prepareBatch("" +
                    "insert into field_definitions(field_id, project) " +
                    "values (:fieldId, :project)");
            assignments.forEach(a -> batch
                    .bind("fieldId", a.getFieldId().getValue())
                    .bind("project", a.getProjectId().map(ProjectId::getValue).orElse(emptyProject))
                    .add());
            batch.execute();
        });
    }

    @RequiredArgsConstructor
    private static class FieldsAssignmentValidator implements AssignmentValidator {
        private final Collection<AvailableFields.FieldDto> availableFields;
        private final Collection<FieldId> toAssignFieldIds;

        @Override
        public Collection<FieldId> missingRequiredFields() {
            var requiredFields = availableFields.stream()
                    .filter(AvailableFields.FieldDto::isRequired)
                    .map(AvailableFields.FieldDto::getId)
                    .collect(Collectors.toSet());
            return requiredFields.stream()
                    .filter(f -> !toAssignFieldIds.contains(f))
                    .collect(Collectors.toList());
        }

        @Override
        public Collection<FieldId> notAvailableFields() {
            var availableFieldIds = availableFields.stream()
                    .map(AvailableFields.FieldDto::getId)
                    .collect(Collectors.toSet());
            return toAssignFieldIds.stream()
                    .filter(f -> !availableFieldIds.contains(f))
                    .collect(Collectors.toList());
        }
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

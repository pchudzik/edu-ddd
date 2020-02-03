package com.pchudzik.edu.ddd.its.field;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class LabelFieldRepository {
    private final Jdbi jdbi;
    private final LastFieldPointerRepository lastFieldPointerRepository;

    public void saveField(LabelField.LabelFieldSnapshot snapshot) {
        jdbi.withHandle(h -> h
                .createUpdate("" +
                        "insert into field(id, version, name, type, description, required) " +
                        "values(:id, :version, :name, :type, :description, :required)")
                .bind("id", snapshot.getFieldId().getValue())
                .bind("version", snapshot.getFieldId().getVersion())
                .bind("type", FieldType.LABEL_FIELD.name())
                .bind("name", snapshot.getFieldName())
                .bind("description", snapshot.getFieldDescription())
                .bind("required", snapshot.isRequired())
                .execute());
        saveLabels(snapshot.getFieldId(), snapshot.getAllowedValues());
        lastFieldPointerRepository.save(snapshot.getFieldId());
    }

    public LabelField findLabelField(FieldId fieldId) {
        return jdbi.withHandle(handle -> {
            var allowedLabels = handle
                    .select("" +
                            "select label.id, label.value " +
                            "from allowed_labels label " +
                            "where " +
                            "    label.field_id = :id " +
                            "    and label.field_version = :version")
                    .bind("id", fieldId.getValue())
                    .bind("version", fieldId.getVersion())
                    .map(new LabelFieldMapper.IdentifiableRowMapper())
                    .list();
            return handle
                    .select("" +
                            "select field.* " +
                            "from field " +
                            "join last_field on field.id = last_field.id and field.version = last_field.version " +
                            "where " +
                            "  field.id = :id " +
                            "  and field.version = :version" +
                            "  and field.type = :labelField")
                    .bind("id", fieldId.getValue())
                    .bind("version", fieldId.getVersion())
                    .bind("labelField", FieldType.LABEL_FIELD)
                    .map(new LabelFieldMapper(allowedLabels))
                    .one();
        });
    }

    private void saveLabels(FieldId fieldId, List<LabelField.LabelFieldSnapshot.Label> allowedValues) {
        jdbi.withHandle(h -> {
            var batch = h.prepareBatch("" +
                    "insert into allowed_labels(id, field_id, field_version, value) " +
                    "values(:id, :fieldId, :fieldVersion, :value)");
            allowedValues.forEach(l -> batch
                    .bind("id", l.getId())
                    .bind("value", l.getValue())
                    .bind("fieldId", fieldId.getValue())
                    .bind("fieldVersion", fieldId.getVersion())
                    .add());

            return batch.execute();
        });
    }

    @RequiredArgsConstructor
    private static class LabelFieldMapper implements RowMapper<LabelField> {
        private final List<LabelField.IdentifiableLabelValue> allowedLabels;

        @Override
        public LabelField map(ResultSet rs, StatementContext ctx) throws SQLException {
            var labelFieldId = new FieldId(
                    UUID.fromString(rs.getString("id")),
                    rs.getInt("version"));
            return new LabelField(
                    labelFieldId,
                    new FieldName(
                            rs.getString("name"),
                            rs.getString("description")),
                    rs.getBoolean("required"),
                    allowedLabels);
        }

        private static class IdentifiableRowMapper implements RowMapper<LabelField.IdentifiableLabelValue> {
            @Override
            public LabelField.IdentifiableLabelValue map(ResultSet rs, StatementContext ctx) throws SQLException {
                return new LabelField.IdentifiableLabelValue(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("value"));
            }
        }
    }
}

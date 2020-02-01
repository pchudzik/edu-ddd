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
class FieldRepository {

    private final Jdbi jdbi;

    public StringField findStringField(FieldId fieldId) {
        return jdbi.withHandle(h -> h
                .select("" +
                        "select field.* " +
                        "from field " +
                        "join last_field on field.id = last_field.id and field.version = last_field.version " +
                        "where " +
                        "  field.id = :id " +
                        "  and last_field.version = :version" +
                        "  and field.type = :stringField")
                .bind("id", fieldId.getValue())
                .bind("version", fieldId.getVersion())
                .bind("stringField", FieldType.STRING_FIELD)
                .map(new StringFieldRowMapper())
                .one());
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
                            "  and last_field.version = :version" +
                            "  and field.type = :labelField")
                    .bind("id", fieldId.getValue())
                    .bind("version", fieldId.getVersion())
                    .bind("labelField", FieldType.LABEL_FIELD)
                    .map(new LabelFieldMapper(allowedLabels))
                    .one();
        });
    }

    private static class StringFieldRowMapper implements RowMapper<StringField> {
        @Override
        public StringField map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new StringField(
                    new FieldId(
                            UUID.fromString(rs.getString("id")),
                            rs.getInt("version")),
                    new FieldName(
                            rs.getString("name"),
                            rs.getString("description")),
                    rs.getBoolean("required"),
                    rs.getInt("min_length"),
                    rs.getInt("max_length"));
        }
    }

    @RequiredArgsConstructor
    private static class LabelFieldMapper implements RowMapper<LabelField> {
        private final List<LabelField.IdentifiableLabelValue> allowedLabels;

        private static class IdentifiableRowMapper implements RowMapper<LabelField.IdentifiableLabelValue> {

            @Override
            public LabelField.IdentifiableLabelValue map(ResultSet rs, StatementContext ctx) throws SQLException {
                return new LabelField.IdentifiableLabelValue(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("value"));
            }
        }
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
    }
}

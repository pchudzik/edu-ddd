package com.pchudzik.edu.ddd.its.field;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldRepositoryImpl {

    private final Jdbi jdbi;

    public <T> Field<T> findOne(FieldId fieldId) {
        final Field<T> field = (Field<T>) jdbi.withHandle(h -> h
                .select("" +
                        "select field.* " +
                        "from field " +
                        "join last_field on field.id = last_field.id and field.version = last_field.version " +
                        "where " +
                        "  field.id = :id " +
                        "  and last_field.version = :version")
                .bind("id", fieldId.getValue())
                .bind("version", fieldId.getVersion())
                .map(selectFieldMapperFromFieldType())
                .one());
        return field;
    }

    private <T> RowMapper<? extends Field<T>> selectFieldMapperFromFieldType() {
        return (rs, ctx) -> {
            final FieldType fieldType = FieldType.valueOf(rs.getString("type"));
            switch (fieldType) {
                case STRING_FIELD:
                    return (Field<T>) new StringFieldRowMapper().map(rs, ctx);
                case LABEL_FIELD:
                    return (Field<T>) new LabelFieldMapper(jdbi).map(rs, ctx);
                default:
                    throw new IllegalArgumentException("Unsupported field type " + fieldType);
            }
        };
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
        private final Jdbi jdbi;

        @Override
        public LabelField map(ResultSet rs, StatementContext ctx) throws SQLException {
            FieldId labelFieldId = new FieldId(
                    UUID.fromString(rs.getString("id")),
                    rs.getInt("version"));
            return new LabelField(
                    labelFieldId,
                    new FieldName(
                            rs.getString("name"),
                            rs.getString("description")),
                    rs.getBoolean("required"),
                    loadLabelValues(labelFieldId));
        }

        private Collection<LabelField.IdentifiableLabelValue> loadLabelValues(FieldId fieldId) {
            return jdbi.withHandle(h -> h
                    .select("" +
                            "select id, value from allowed_labels " +
                            "where field_id = :id and field_version = :version")
                    .bind("id", fieldId.getValue())
                    .bind("version", fieldId.getVersion())
                    .map((rs, ctx) -> new LabelField.IdentifiableLabelValue(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("value")))
                    .list());
        }
    }
}

package com.pchudzik.edu.ddd.its.field;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class StringFieldRepository {
    private final Jdbi jdbi;

    private final LastFieldPointerRepository lastFieldPointerRepository;

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

    public void save(StringField.StringFieldSnapshot fieldSnapshot) {
        jdbi.withHandle(h ->
                h
                        .createUpdate("" +
                                "insert into field" +
                                "(id,  version,  type,  name,  description,  required,  min_length,  max_length) values " +
                                "(:id, :version, :type, :name, :description, :required, :min_length, :max_length)")
                        .bind("id", fieldSnapshot.getFieldId().getValue())
                        .bind("version", fieldSnapshot.getFieldId().getVersion())
                        .bind("type", FieldType.STRING_FIELD.name())
                        .bind("name", fieldSnapshot.getFieldName())
                        .bind("description", fieldSnapshot.getFieldDescription())
                        .bind("required", fieldSnapshot.getConfiguration().isRequired())
                        .bind("min_length", fieldSnapshot.getConfiguration().getMinLength())
                        .bind("max_length", fieldSnapshot.getConfiguration().getMaxLength())
                        .execute());
        lastFieldPointerRepository.save(fieldSnapshot.getFieldId());
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
}

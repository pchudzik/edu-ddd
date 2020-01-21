package com.pchudzik.edu.ddd.its.field;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldRepositoryImpl implements FieldRepository {

    private final Jdbi jdbi;

    @Override
    public <T> CustomField<T> findOne(FieldId fieldId) {
        final CustomField<T> field = jdbi.withHandle(h -> h
                .select("" +
                        "select field.* " +
                        "from field " +
                        "join last_field on field.id = last_field.id and field.version = last_field.version " +
                        "where field.id = :id ")
                .bind("id", fieldId.getValue())
                .map(selectFieldMapperFromFieldType())
                .one());
    }

    private <T> RowMapper<CustomField<T>> selectFieldMapperFromFieldType() {
        return (rs, ctx) -> {
            final FieldType fieldType = FieldType.valueOf(rs.getString("type"));
            switch (fieldType) {
                case STRING_FIELD:
                    return new StringFieldRowMapper().map(rs, ctx);
                default:
                    throw new IllegalArgumentException("Unsupported field type " + fieldType);
            }
        };
    }

    private static class StringFieldRowMapper implements RowMapper<StringField> {

        @Override
        public StringField map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new StringField(
                    new FieldId(UUID.fromString(rs.getString("id"))),
                    new FieldVersion(rs.getInt("version")),
                    new FieldName(
                            rs.getString("name"),
                            rs.getString("description")),
                    rs.getBoolean("required"),
                    rs.getInt("min_length"),
                    rs.getInt("max_length"));
        }
    }
}

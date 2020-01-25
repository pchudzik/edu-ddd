package com.pchudzik.edu.ddd.its.field;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldReadFacadeImpl implements FieldReadFacade {
    private final Jdbi jdbi;

    @Override
    public List<FieldDto> listAllFields() {
        return jdbi.withHandle(h -> h
                .select("select field.* " +
                        "from field " +
                        "join last_field on field.id = last_field.id and field.version = last_field.version")
                .map(new FieldDtoMapper())
                .list());
    }

    private static class FieldDtoMapper implements RowMapper<FieldDto> {
        @Override
        public FieldDto map(ResultSet rs, StatementContext ctx) throws SQLException {
            FieldType fieldType = FieldType.valueOf(rs.getString("type"));
            return FieldDto.builder()
                    .id(new FieldId(
                            UUID.fromString(rs.getString("id")),
                            rs.getInt("version")))
                    .type(fieldType)
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .configuration(resolveFieldConfigurationMapper(fieldType).map(rs, ctx))
                    .build();
        }

        private RowMapper<Map<String, Object>> resolveFieldConfigurationMapper(FieldType fieldType) {
            switch (fieldType) {
                case STRING_FIELD:
                    return new StringFieldConfigurationMapper();
                default:
                    throw new IllegalArgumentException("Unsupported field type " + fieldType);
            }
        }
    }

    private static class StringFieldConfigurationMapper implements RowMapper<Map<String, Object>> {

        @Override
        public Map<String, Object> map(ResultSet rs, StatementContext ctx) throws SQLException {
            return Map.of(
                    "required", rs.getBoolean("required"),
                    "minLength", rs.getInt("min_length"),
                    "maxLength", rs.getInt("max_length"));
        }
    }
}

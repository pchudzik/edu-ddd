package com.pchudzik.edu.ddd.its.field;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldReadFacadeImpl implements FieldReadFacade {

    private final Jdbi jdbi;

    @Override
    public List<FieldDto> listAllFields() {
        List<FieldDto> fields = jdbi.withHandle(h -> h
                .select("select field.* " +
                        "from field " +
                        "join last_field on field.id = last_field.id and field.version = last_field.version")
                .map(new FieldDtoMapper())
                .list());

        loadAllowedLabelsForLabelFields(fields);

        return fields;
    }

    private void loadAllowedLabelsForLabelFields(List<FieldDto> fields) {
        Set<FieldDto> labelFields = fields.stream()
                .filter(field -> FieldType.LABEL_FIELD == field.getType())
                .collect(Collectors.toSet());

        if(labelFields.isEmpty()) {
            return;
        }

        Set<FieldId> ids = labelFields.stream()
                .map(FieldDto::getId)
                .collect(Collectors.toSet());
        Map<FieldId, AllowedLabel> allowedLabels = jdbi.withHandle(h -> h
                .select("" +
                        "select id, field_id, field_version, value " +
                        "from allowed_labels" +
                        "where " +
                        "    field_id in :ids " +
                        "    and field_version in :versions ")
                .bindList("ids", ids.stream().map(FieldId::getValue).collect(toList()))
                .bindList("versions", ids.stream().map(FieldId::getValue).collect(toList()))
                .map((rs, ctx) -> new AllowedLabel(
                        new FieldId(
                                UUID.fromString(rs.getString("field_id")),
                                rs.getInt("field_version")),
                        UUID.fromString(rs.getString("id")),
                        rs.getString("value")))
                .list())
                .stream()
                .collect(toMap(l -> l.fieldId, Function.identity()));
    }

    @RequiredArgsConstructor
    private static class AllowedLabel {

        private final FieldId fieldId;

        private final UUID labelId;

        private final String value;
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
                case LABEL_FIELD:
                    return new LabelFieldConfigurationMapper();
                default:
                    throw new IllegalArgumentException("Unsupported field type " + fieldType);
            }
        }

        private class LabelFieldConfigurationMapper implements RowMapper<Map<String, Object>> {

            @Override
            public Map<String, Object> map(ResultSet rs, StatementContext ctx) throws SQLException {
                return Map.of(
                        "required", rs.getBoolean("required"),
                        "allowedValues", emptyList());
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

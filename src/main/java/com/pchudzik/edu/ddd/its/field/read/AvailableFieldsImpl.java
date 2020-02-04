package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.FieldType;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class AvailableFieldsImpl implements AvailableFields {

    private final Jdbi jdbi;

    @Override
    public List<FieldDto> listAllFields() {
        return jdbi.withHandle(h -> Stream
                .concat(
                        new GenericFieldDtoLoader(h).loadFields(),
                        new LabelFieldDtoLoader(h).loadLabelFields())
                .collect(Collectors.toList()));
    }

    @Override
    public List<FieldDto> findByIds(List<FieldId> ids) {
        return emptyList();
    }

    @RequiredArgsConstructor
    private static class GenericFieldDtoLoader {
        private final Handle handle;

        Stream<FieldDto> loadFields() {
            return handle
                    .select("select field.* " +
                            "from field " +
                            "join last_field on field.id = last_field.id and field.version = last_field.version " +
                            "where type not in(<excluded>)")
                    .bindList("excluded", singleton(FieldType.LABEL_FIELD))
                    .map(new FieldDtoMapper())
                    .stream();
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
    }

    @RequiredArgsConstructor
    private static class LabelFieldDtoLoader {
        private final Handle handle;

        Stream<FieldDto> loadLabelFields() {
            List<TmpField> labelFields = handle
                    .select("select field.* " +
                            "from field " +
                            "join last_field on field.id = last_field.id and field.version = last_field.version " +
                            "where type = :labelField")
                    .bind("labelField", FieldType.LABEL_FIELD)
                    .map(new TmpFieldMapper())
                    .list();
            Map<FieldId, List<AllowedLabel>> allowedLabels = loadAllowedLabels(labelFields.stream().map(f -> f.fieldId).collect(toList()));
            return labelFields.stream()
                    .map(f -> createFieldDto(f, allowedLabels.getOrDefault(f.fieldId, emptyList())));
        }

        private FieldDto createFieldDto(TmpField tmpField, List<AllowedLabel> allowedLabels) {
            return new FieldDto(
                    FieldType.LABEL_FIELD,
                    tmpField.fieldId,
                    tmpField.name,
                    tmpField.description,
                    Map.of(
                            "required", tmpField.required,
                            "allowedLabels", allowedLabels.stream()
                                    .map(this::allowedLabelAsConfiguration)
                                    .collect(toList())));
        }

        private Map<String, Object> allowedLabelAsConfiguration(AllowedLabel allowedLabel) {
            return Map.of(
                    "id", allowedLabel.labelId,
                    "value", allowedLabel.value);
        }

        private Map<FieldId, List<AllowedLabel>> loadAllowedLabels(List<FieldId> ids) {
            if (ids.isEmpty()) {
                return emptyMap();
            }

            return handle
                    .select("" +
                            "select id, field_id, field_version, value " +
                            "from allowed_labels " +
                            "where " +
                            "    field_id in (<ids>) " +
                            "    and field_version in (<versions>) " +
                            "order by lower(value)")
                    .bindList("ids", ids.stream().map(FieldId::getValue).collect(toList()))
                    .bindList("versions", ids.stream().map(FieldId::getVersion).collect(toList()))
                    .map((rs, ctx) -> new AllowedLabel(
                            new FieldId(
                                    UUID.fromString(rs.getString("field_id")),
                                    rs.getInt("field_version")),
                            UUID.fromString(rs.getString("id")),
                            rs.getString("value")))
                    .list()
                    .stream()
                    .collect(Collectors.groupingBy(l -> l.fieldId));
        }

        @RequiredArgsConstructor
        private static class TmpField {
            private final FieldId fieldId;
            private final String name;
            private final String description;
            private final boolean required;
        }

        @RequiredArgsConstructor
        private static class AllowedLabel {
            private final FieldId fieldId;
            private final UUID labelId;
            private final String value;
        }

        private static class TmpFieldMapper implements RowMapper<TmpField> {

            @Override
            public TmpField map(ResultSet rs, StatementContext ctx) throws SQLException {
                return new TmpField(
                        new FieldId(
                                UUID.fromString(rs.getString("id")),
                                rs.getInt("version")),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBoolean("required"));
            }
        }
    }
}

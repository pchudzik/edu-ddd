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
import java.util.*;
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
                        new LabelFieldDtoLoader(h).loadFields())
                .collect(Collectors.toList()));
    }

    @Override
    public List<FieldDto> findByIds(Collection<FieldId> ids) {
        var idsSet = new HashSet<>(ids);
        return jdbi.withHandle(h -> Stream
                .concat(
                        new GenericFieldDtoLoader(h).loadFields(idsSet),
                        new LabelFieldDtoLoader(h).loadFields(idsSet))
                .collect(Collectors.toList()));
    }

    private interface FieldLoader {
        Stream<FieldDto> loadFields(Set<FieldId> fieldIds);

        Stream<FieldDto> loadFields();
    }

    @RequiredArgsConstructor
    private static class GenericFieldDtoLoader implements FieldLoader {
        private final Handle handle;

        @Override
        public Stream<FieldDto> loadFields() {
            return handle
                    .select("select field.* " +
                            "from field " +
                            "join last_field on field.id = last_field.id and field.version = last_field.version " +
                            "where type not in(<excluded>)")
                    .bindList("excluded", singleton(FieldType.LABEL_FIELD))
                    .map(new FieldDtoMapper())
                    .stream();
        }

        @Override
        public Stream<FieldDto> loadFields(Set<FieldId> fieldIds) {
            if (fieldIds.isEmpty()) {
                return Stream.empty();
            }

            return handle
                    .select("select field.* " +
                            "from field " +
                            "join last_field on field.id = last_field.id and field.version = last_field.version " +
                            "where " +
                            "    type not in(<excluded>) " +
                            "    and field.id in(<ids>) " +
                            "    and field.version in (<versions>)")
                    .bindList("excluded", singleton(FieldType.LABEL_FIELD))
                    .bindList("ids", fieldIds.stream().map(FieldId::getValue).collect(Collectors.toSet()))
                    .bindList("versions", fieldIds.stream().map(FieldId::getVersion).collect(Collectors.toSet()))
                    .map(new FieldDtoMapper())
                    .stream()
                    .filter(f -> fieldIds.contains(f.getId()));
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
                        .required(rs.getBoolean("required"))
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
                            "minLength", rs.getInt("min_length"),
                            "maxLength", rs.getInt("max_length"));
                }
            }
        }
    }

    @RequiredArgsConstructor
    private static class LabelFieldDtoLoader implements FieldLoader {
        private final Handle handle;

        @Override
        public Stream<FieldDto> loadFields() {
            return processLoadedFields(handle
                    .select("select field.* " +
                            "from field " +
                            "join last_field on field.id = last_field.id and field.version = last_field.version " +
                            "where type = :labelField")
                    .bind("labelField", FieldType.LABEL_FIELD)
                    .map(new TmpFieldMapper())
                    .list());
        }

        @Override
        public Stream<FieldDto> loadFields(Set<FieldId> fieldIds) {
            if (fieldIds.isEmpty()) {
                return Stream.empty();
            }
            return processLoadedFields(handle
                    .select("select field.* " +
                            "from field " +
                            "join last_field on field.id = last_field.id and field.version = last_field.version " +
                            "where " +
                            "    type = :labelField" +
                            "    and field.id in (<ids>)" +
                            "    and field.version in (<versions>)")
                    .bind("labelField", FieldType.LABEL_FIELD)
                    .bindList("ids", fieldIds.stream().map(FieldId::getValue).collect(toList()))
                    .bindList("versions", fieldIds.stream().map(FieldId::getVersion).collect(Collectors.toSet()))
                    .map(new TmpFieldMapper())
                    .stream()
                    .filter(f -> fieldIds.contains(f.fieldId))
                    .collect(toList()));
        }

        private Stream<FieldDto> processLoadedFields(List<TmpField> fieldsWithoutLabels) {
            Map<FieldId, List<AllowedLabel>> allowedLabels = loadAllowedLabels(fieldsWithoutLabels.stream().map(f -> f.fieldId).collect(toList()));
            return fieldsWithoutLabels.stream()
                    .map(f -> createFieldDto(f, allowedLabels.getOrDefault(f.fieldId, emptyList())));
        }

        private FieldDto createFieldDto(TmpField tmpField, List<AllowedLabel> allowedLabels) {
            return new FieldDto(
                    FieldType.LABEL_FIELD,
                    tmpField.fieldId,
                    tmpField.name,
                    tmpField.description,
                    tmpField.required,
                    Map.of(
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

package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.FieldType;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldValuesReadRepository {
    private static final Map<FieldType, FieldValuesTransformer<?>> valueTransformers = Map.of(
            FieldType.STRING_FIELD, new StringFieldTransformer(),
            FieldType.LABEL_FIELD, new LabelFieldTransformer());

    private final Jdbi jdbi;

    public List<FieldValues.FieldValueDto<?>> findValues(IssueId issueId) {
        return jdbi.withHandle(handle -> findAllFields(handle, issueId)
                .collect(Collectors.groupingBy(ValueRow::getFieldType))
                .entrySet()
                .stream()
                .map(entry -> valueTransformers.get(entry.getKey()).apply(entry.getValue()))
                .reduce(Stream::concat)
                .orElse(Stream.empty())
                .collect(toList()));
    }


    private Stream<ValueRow> findAllFields(Handle handle, IssueId issueId) {
        return handle
                .select("" +
                        "select " +
                        "  value.id id, " +
                        "  value.field_id fieldId, " +
                        "  last_field.version version, " +
                        "  value.project project, " +
                        "  value.issue issue, " +
                        "  value.value value, " +
                        "  field.type type " +
                        "from field_value value " +
                        "join last_field last_field on last_field.id = value.field_id " +
                        "join field field on field.id = value.field_id " +
                        "where " +
                        "  project=:project " +
                        "  and issue=:issue ")
                .bind("project", issueId.getProject().getValue())
                .bind("issue", issueId.getIssue())
                .map((rs, ctx) -> new ValueRow(
                        UUID.fromString(rs.getString("id")),
                        new FieldId(
                                UUID.fromString(rs.getString("fieldId")),
                                rs.getInt("version")),
                        new IssueId(
                                new ProjectId(rs.getString("project")),
                                rs.getInt("issue")),
                        FieldType.valueOf(rs.getString("type")),
                        rs.getString("value")))
                .stream();
    }

    private interface FieldValuesTransformer<T> extends Function<List<ValueRow>, Stream<FieldValues.FieldValueDto<T>>> {
    }

    @Getter
    @RequiredArgsConstructor
    private static class ValueRow {
        private final UUID valueId;
        private final FieldId fieldId;
        private final IssueId issueId;
        private final FieldType fieldType;
        private final String value;
    }

    private static class StringFieldTransformer implements FieldValuesTransformer<FieldValues.StringValue> {
        @Override
        public Stream<FieldValues.FieldValueDto<FieldValues.StringValue>> apply(List<ValueRow> fieldValueDtos) {
            return fieldValueDtos.stream()
                    .map(r -> new FieldValues.FieldValueDto<>(
                            r.getFieldId(),
                            r.getIssueId(),
                            r.getFieldType(),
                            new FieldValues.StringValue(
                                    r.getValueId(),
                                    r.getValue())));
        }
    }

    private static class LabelFieldTransformer implements FieldValuesTransformer<FieldValues.LabelValues> {
        @Override
        public Stream<FieldValues.FieldValueDto<FieldValues.LabelValues>> apply(List<ValueRow> fieldValueDtos) {
            return fieldValueDtos.stream()
                    .collect(Collectors.groupingBy(LabelGroupKey::from))
                    .entrySet().stream()
                    .map(entry -> {
                        LabelGroupKey labelKey = entry.getKey();
                        List<FieldValues.LabelValue> labelValues = entry.getValue().stream()
                                .map(r -> new FieldValues.LabelValue(
                                        r.getValueId(),
                                        r.getValue()))
                                .collect(toList());
                        return new FieldValues.FieldValueDto<>(
                                labelKey.fieldId,
                                labelKey.issueId,
                                labelKey.fieldType,
                                new FieldValues.LabelValues(labelValues));
                    });
        }

        @EqualsAndHashCode
        @RequiredArgsConstructor
        private static class LabelGroupKey {
            private final FieldId fieldId;
            private final IssueId issueId;
            private final FieldType fieldType;

            static LabelGroupKey from(ValueRow valueDto) {
                return new LabelGroupKey(
                        valueDto.getFieldId(),
                        valueDto.getIssueId(),
                        valueDto.getFieldType());
            }
        }
    }
}

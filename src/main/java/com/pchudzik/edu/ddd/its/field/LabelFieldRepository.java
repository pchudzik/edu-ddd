package com.pchudzik.edu.ddd.its.field;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class LabelFieldRepository {
    private final Jdbi jdbi;
    private final LastFieldPointerRepository lastFieldPointerRepository;

    public void saveLabels(FieldId fieldId, List<LabelField.LabelFieldSnapshot.Label> allowedValues) {
        List<UUID> ids = allowedValues.stream()
                .map(LabelField.LabelFieldSnapshot.Label::getId)
                .collect(Collectors.toList());
        List<String> values = allowedValues.stream()
                .map(LabelField.LabelFieldSnapshot.Label::getValue)
                .collect(Collectors.toList());
        jdbi.withHandle(h -> {
            PreparedBatch batch = h.prepareBatch("" +
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
        lastFieldPointerRepository.save(snapshot.getFieldId());
    }
}

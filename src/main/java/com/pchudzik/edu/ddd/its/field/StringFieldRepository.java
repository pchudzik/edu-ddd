package com.pchudzik.edu.ddd.its.field;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class StringFieldRepository {
    private final Jdbi jdbi;

    private final LastFieldPointerRepository lastFieldPointerRepository;

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
}

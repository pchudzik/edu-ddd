package com.pchudzik.edu.ddd.its.field;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class LastFieldPointerRepository {
    private final Jdbi jdbi;

    public void save(FieldId fieldId) {
        boolean isUpdated = jdbi.withHandle(tryToExecuteUpdate(fieldId));
        if (!isUpdated) {
            jdbi.withHandle(executeInsert(fieldId));
        }
    }

    private HandleCallback<Boolean, RuntimeException> tryToExecuteUpdate(FieldId fieldId) {
        return h -> h
                .createUpdate("" +
                        "update last_field " +
                        "set version = :version " +
                        "where id = :id")
                .bind("id", fieldId.getValue())
                .bind("version", fieldId.getVersion())
                .execute((statementSupplier, ctx) -> statementSupplier.get().getUpdateCount() == 1);
    }

    private HandleCallback<Integer, RuntimeException> executeInsert(FieldId fieldId) {
        return h -> h
                .createUpdate("insert into last_field (id, version) values(:id, :version)")
                .bind("id", fieldId.getValue())
                .bind("version", fieldId.getVersion())
                .execute();
    }
}

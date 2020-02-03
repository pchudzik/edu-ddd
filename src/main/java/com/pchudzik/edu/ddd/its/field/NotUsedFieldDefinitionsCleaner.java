package com.pchudzik.edu.ddd.its.field;

import com.google.common.eventbus.Subscribe;
import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldUpdateListener implements MessageQueue.MessageListener {
    private final TransactionManager txManager;
    private final NoLongerUsedFieldDefinitionCleanerRepository cleaner;

    @Subscribe
    public void onDefinitionUpdate(FieldDefinitionUpdatedMessage updatedMessage) {
        deleteNoLongerUsedFieldDefinitions(updatedMessage.getFieldId());
    }

    @Subscribe
    public void onFieldAssigned(FieldAssignedMessage fieldAssignedMessage) {
        fieldAssignedMessage
                .getValueIds().stream()
                .map(FieldValueId::getField)
                .distinct()
                .forEach(this::deleteNoLongerUsedFieldDefinitions);
    }

    private void deleteNoLongerUsedFieldDefinitions(FieldId fieldId) {
        txManager.useTransaction(() -> cleaner.deleteNoLongerUsedFieldsButVersion(fieldId));
    }
}

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class NoLongerUsedFieldDefinitionCleanerRepository {

    private final Jdbi jdbi;

    public void deleteNoLongerUsedFieldsButVersion(FieldId fieldId) {
        jdbi.withHandle(h -> h
                .createUpdate("" +
                        "delete from field " +
                        "where " +
                        "    id = :id " +
                        "    and version != :version " +
                        "    and not exists (" +
                        "      select value.id " +
                        "      from field_value value " +
                        "      where " +
                        "          value.field_id = :id" +
                        "          and value.field_version != :version " +
                        "    )")
                .bind("id", fieldId.getValue())
                .bind("version", fieldId.getVersion())
                .execute());
    }
}

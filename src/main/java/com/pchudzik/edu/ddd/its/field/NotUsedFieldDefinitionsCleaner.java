package com.pchudzik.edu.ddd.its.field;

import com.google.common.eventbus.Subscribe;
import com.pchudzik.edu.ddd.its.field.defaults.assignment.Messages;
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

    @Subscribe
    public void onFieldAssignmentRemoval(Messages.FieldAssignmentRemovedFromProject fieldAssignmentRemoval) {
        deleteNoLongerUsedFieldDefinitions(fieldAssignmentRemoval.getFieldId());
    }

    @Subscribe
    public void onFieldAssignmentRemoval(Messages.FieldAssignmentRemovedFromIssue fieldAssignmentRemovedFromIssue) {
        deleteNoLongerUsedFieldDefinitions(fieldAssignmentRemovedFromIssue.getFieldId());
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
                        "    and not exists (" +
                        "      select value.id " +
                        "      from field_value value " +
                        "      where " +
                        "          value.field_id = :id" +
                        "          and value.field_version = field.version " +
                        "    )" +
                        "    and not exists (" +
                        "        select field_definitions.field_id " +
                        "        from field_definitions" +
                        "        where " +
                        "            field_definitions.field_id = :id " +
                        "            and field_definitions.field_version = field.version " +
                        "    )" +
                        "    and not exists (" +
                        "        select last_field.id " +
                        "        from last_field " +
                        "        where " +
                        "            last_field.id = :id " +
                        "            and last_field.version = field.version " +
                        "    )")
                .bind("id", fieldId.getValue())
                .bind("version", fieldId.getVersion())
                .execute());
    }
}

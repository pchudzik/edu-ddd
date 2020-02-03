package com.pchudzik.edu.ddd.its.field;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;
import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldUpdateListener implements MessageQueue.MessageListener {
    private final TransactionManager txManager;
    private final NoLongerUsedFieldDefinitionCleaner cleaner;

    @Subscribe
    public void onFieldRemoval(FieldUpdatedMessage fieldUpdated) {
        txManager.useTransaction(() -> cleaner.deleteNoLongerUsedFieldsButVersion(fieldUpdated.getFieldId()));
    }
}

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class NoLongerUsedFieldDefinitionCleaner {

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
                        "      where value.field_id = :id" +
                        "    )")
                .bind("id", fieldId.getValue())
                .bind("version", fieldId.getVersion())
                .execute());
    }
}

@Getter
@RequiredArgsConstructor
class FieldUpdatedMessage implements MessageQueue.Message {

    private final FieldId fieldId;
}

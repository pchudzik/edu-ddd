package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
class FieldDefinitionUpdatedMessage implements MessageQueue.Message {
    private final FieldId fieldId;
}

@Getter
@RequiredArgsConstructor
class FieldAssignedMessage implements MessageQueue.Message {
    private final Collection<FieldValueId> valueIds;
}
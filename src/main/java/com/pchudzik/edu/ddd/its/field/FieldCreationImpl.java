package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldCreationImpl implements FieldCreation {
    private final TransactionManager txManager;
    private final StringFieldRepository stringFieldRepository;
    private final LabelFieldRepository labelFieldRepository;
    private final MessageQueue messageQueue;

    @Override
    public FieldId createStringField(StringFieldCreationCommand command) {
        return txManager.inTransaction(() -> {
            var field = new StringField(
                    new FieldId(),
                    new FieldName(command.getFieldName(), command.getFieldDescription()),
                    command.isRequired(), command.getMinLength(), command.getMaxLength());
            stringFieldRepository.save(field.getSnapshot());
            return field.getFieldId();
        });
    }

    @Override
    public FieldId updateStringField(FieldId fieldId, StringFieldConfigurationUpdateCommand cmd) {
        return txManager.inTransaction(() -> {
            var stringField = stringFieldRepository.findStringField(fieldId);
            stringField.applyConfiguration(cmd);
            stringFieldRepository.save(stringField.getSnapshot());
            messageQueue.publish(new FieldDefinitionUpdatedMessage(stringField.getFieldId()));
            return stringField.getFieldId();
        });
    }

    @Override
    public FieldId createLabelField(LabelFieldCreationCommand fieldCreationCommand) {
        return txManager.inTransaction(() -> {
            var label = new LabelField(
                    new FieldId(),
                    new FieldName(fieldCreationCommand.getFieldName(), fieldCreationCommand.getFieldDescription()),
                    fieldCreationCommand.isRequired(),
                    fieldCreationCommand.getAllowedValues().stream()
                            .map(l -> new LabelField.IdentifiableLabelValue(UUID.randomUUID(), l))
                            .collect(Collectors.toSet()));
            LabelField.LabelFieldSnapshot snapshot = label.getSnapshot();
            labelFieldRepository.saveField(snapshot);
            return label.getFieldId();
        });
    }

    @Override
    public FieldId updateLabelField(FieldId fieldId, LabelFieldConfigurationUpdateCommand cmd) {
        return txManager.inTransaction(() -> {
            var labelField = labelFieldRepository.findLabelField(fieldId);
            labelField.applyConfiguration(cmd);
            LabelField.LabelFieldSnapshot snapshot = labelField.getSnapshot();
            labelFieldRepository.saveField(snapshot);
            messageQueue.publish(new FieldDefinitionUpdatedMessage(labelField.getFieldId()));
            return labelField.getFieldId();
        });
    }
}

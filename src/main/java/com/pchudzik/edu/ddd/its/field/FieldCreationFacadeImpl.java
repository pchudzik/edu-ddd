package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldCreationFacadeImpl implements FieldCreationFacade {
    private final TransactionManager txManager;
    private final StringFieldRepository stringFieldRepository;
    private final LastFieldPointerRepository lastFieldPointerRepository;

    @Override
    public FieldId createStringField(StringFieldCreationCommand command) {
        return txManager.inTransaction(() -> {
            StringField field = new StringField(
                    new FieldId(),
                    new FieldName(command.getFieldName(), command.getFieldDescription()),
                    command.isRequired(), command.getMinLength(), command.getMaxLength());
            stringFieldRepository.save(field.getSnapshot());
            lastFieldPointerRepository.save(field.getFieldId(), field.getFieldVersion());
            return field.getFieldId();
        });
    }
}

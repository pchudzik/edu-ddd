package com.pchudzik.edu.ddd.its.field;

import com.google.inject.AbstractModule;

public class FieldContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldCreation.class).to(FieldCreationImpl.class);
        bind(FieldAssignment.class).to(FieldAssignmentImpl.class);

        bind(FieldValueRepository.class);
        bind(LabelFieldRepository.class);
        bind(NoLongerUsedFieldDefinitionCleanerRepository.class);
        bind(FieldUpdateListener.class);
    }
}

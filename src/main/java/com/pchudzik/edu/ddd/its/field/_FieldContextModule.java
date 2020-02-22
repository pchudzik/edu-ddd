package com.pchudzik.edu.ddd.its.field;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class _FieldContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldCreation.class).to(FieldCreationImpl.class).in(Singleton.class);
        bind(FieldAssignment.class).to(FieldAssignmentImpl.class).in(Singleton.class);

        bind(FieldValueRepository.class).in(Singleton.class);
        bind(LabelFieldRepository.class).in(Singleton.class);
        bind(NoLongerUsedFieldDefinitionCleanerRepository.class).in(Singleton.class);
        bind(FieldUpdateListener.class).in(Singleton.class);
    }
}

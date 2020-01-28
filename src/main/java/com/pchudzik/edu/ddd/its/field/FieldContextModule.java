package com.pchudzik.edu.ddd.its.field;

import com.google.inject.AbstractModule;

public class FieldContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldCreationFacade.class).to(FieldCreationFacadeImpl.class);
        bind(FieldAssignmentFacade.class).to(FieldAssignmentFacadeImpl.class);
        bind(FieldValuesFacade.class).to(FieldValuesFacadeImpl.class);
        bind(FieldReadFacade.class).to(FieldReadFacadeImpl.class);

        bind(FieldRepositoryImpl.class);
        bind(FieldValueRepository.class);
        bind(LabelFieldRepository.class);
    }
}

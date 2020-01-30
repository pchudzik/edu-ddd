package com.pchudzik.edu.ddd.its.field.read;

import com.google.inject.AbstractModule;

public class FieldReadContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldValuesFacade.class).to(FieldValuesFacadeImpl.class);
        bind(FieldReadFacade.class).to(FieldReadFacadeImpl.class);
        bind(FieldValuesReadRepository.class);
    }
}

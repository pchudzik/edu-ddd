package com.pchudzik.edu.ddd.its.field;

import com.google.inject.AbstractModule;

public class FieldContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldCreationFacade.class).to(FieldCreationFacadeImpl.class);
        bind(FieldReadFacade.class).to(FieldReadFacadeImpl.class);
    }
}

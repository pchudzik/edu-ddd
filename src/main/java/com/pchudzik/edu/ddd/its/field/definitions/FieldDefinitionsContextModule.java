package com.pchudzik.edu.ddd.its.field.definitions;

import com.google.inject.AbstractModule;

public class FieldDefinitionsContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldDefinitions.class).to(FieldDefinitionsImpl.class);
    }
}

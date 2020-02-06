package com.pchudzik.edu.ddd.its.field.defaults.assignment;

import com.google.inject.AbstractModule;

public class FieldDefinitionsContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldDefinitions.class).to(FieldDefinitionsImpl.class);
    }
}
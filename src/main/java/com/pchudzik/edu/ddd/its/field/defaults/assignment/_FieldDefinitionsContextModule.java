package com.pchudzik.edu.ddd.its.field.defaults.assignment;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class _FieldDefinitionsContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldDefinitions.class).to(FieldDefinitionsImpl.class).in(Singleton.class);
    }
}

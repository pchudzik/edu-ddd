package com.pchudzik.edu.ddd.its.field.read;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class _FieldReadContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldValues.class).to(FieldValuesImpl.class).in(Singleton.class);
        bind(AvailableFields.class).to(AvailableFieldsImpl.class).in(Singleton.class);
        bind(FieldValuesReadRepository.class).in(Singleton.class);
    }
}

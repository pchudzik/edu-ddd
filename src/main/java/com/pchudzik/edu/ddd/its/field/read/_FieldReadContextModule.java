package com.pchudzik.edu.ddd.its.field.read;

import com.google.inject.AbstractModule;

public class _FieldReadContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FieldValues.class).to(FieldValuesImpl.class);
        bind(AvailableFields.class).to(AvailableFieldsImpl.class);
        bind(FieldValuesReadRepository.class);
    }
}

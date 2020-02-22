package com.pchudzik.edu.ddd.its.user.access;

import com.google.inject.AbstractModule;

public class _AccessContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Access.class).to(AccessImpl.class);
    }
}

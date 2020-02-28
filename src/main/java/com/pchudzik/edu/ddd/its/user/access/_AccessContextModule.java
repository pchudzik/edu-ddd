package com.pchudzik.edu.ddd.its.user.access;

import com.google.inject.AbstractModule;

public class _AccessContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Access.class).to(AccessImpl.class);
        bind(RolePermissionsRepository.class).to(RolePermissionsRepositoryImpl.class);
        bind(PermissionsRepository.class).to(PermissionsRepositoryImpl.class);
    }
}

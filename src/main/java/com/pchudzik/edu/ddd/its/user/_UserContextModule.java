package com.pchudzik.edu.ddd.its.user;

import com.google.inject.AbstractModule;

public class _UserContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserRepository.class).to(UserRepositoryImpl.class);
        bind(Users.class).to(UsersImpl.class);
    }
}

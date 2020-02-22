package com.pchudzik.edu.ddd.its.project;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class _ProjectContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProjectCreation.class).to(ProjectCreationImpl.class).in(Singleton.class);
        bind(ProjectRepository.class).to(ProjectRepositoryImpl.class).in(Singleton.class);
    }
}

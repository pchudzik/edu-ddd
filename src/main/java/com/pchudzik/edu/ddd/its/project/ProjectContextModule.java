package com.pchudzik.edu.ddd.its.project;

import com.google.inject.AbstractModule;

public class ProjectContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProjectCreation.class).to(ProjectCreationImpl.class);
        bind(ProjectRepository.class).to(ProjectRepositoryImpl.class);
    }
}

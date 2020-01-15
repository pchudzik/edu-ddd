package com.pchudzik.edu.ddd.its.project.read;

import com.google.inject.AbstractModule;

public class ProjectReadContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProjectViewFacade.class).to(ProjectViewFacadeImpl.class);
        bind(ProjectViewRepository.class).to(ProjectViewRepositoryImpl.class);
    }
}

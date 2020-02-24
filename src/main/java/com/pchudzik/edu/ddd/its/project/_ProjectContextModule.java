package com.pchudzik.edu.ddd.its.project;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainService;

public class _ProjectContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProjectCreation.class).annotatedWith(DomainService.class).to(ProjectCreationImpl.class).in(Singleton.class);
        bind(ProjectCreation.class).to(ProjectCreationAppService.class).in(Singleton.class);
        bind(ProjectRepository.class).to(ProjectRepositoryImpl.class).in(Singleton.class);
    }
}

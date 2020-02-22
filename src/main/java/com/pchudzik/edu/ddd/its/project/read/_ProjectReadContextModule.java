package com.pchudzik.edu.ddd.its.project.read;

import com.google.inject.AbstractModule;

public class _ProjectReadContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProjectView.class).to(ProjectViewImpl.class);
        bind(ProjectViewRepository.class).to(ProjectViewRepositoryImpl.class);
    }
}

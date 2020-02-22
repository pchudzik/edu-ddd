package com.pchudzik.edu.ddd.its.project.read;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class _ProjectReadContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProjectView.class).to(ProjectViewImpl.class).in(Singleton.class);
        bind(ProjectViewRepository.class).to(ProjectViewRepositoryImpl.class).in(Singleton.class);
    }
}

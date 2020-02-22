package com.pchudzik.edu.ddd.its.issue.id;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class _IssueIdContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IssueIdGeneratorRepository.class).in(Singleton.class);
        bind(ProjectCreationListener.class).in(Singleton.class);
        bind(IssueIdGenerator.class).to(IssueIdGeneratorImpl.class).in(Singleton.class);
    }
}

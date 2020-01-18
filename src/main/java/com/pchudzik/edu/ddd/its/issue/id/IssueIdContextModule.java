package com.pchudzik.edu.ddd.its.issue.id;

import com.google.inject.AbstractModule;

public class IssueIdContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IssueIdGeneratorRepository.class);
        bind(ProjectCreationListener.class);
        bind(IssueIdGenerator.class).to(IssueIdGeneratorImpl.class);
    }
}

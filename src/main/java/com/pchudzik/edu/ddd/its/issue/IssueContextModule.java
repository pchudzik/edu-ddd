package com.pchudzik.edu.ddd.its.issue;

import com.google.inject.AbstractModule;

public class IssueContextModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IssueCreation.class).to(IssueCreationImpl.class);
        bind(IssueRepository.class);
    }
}

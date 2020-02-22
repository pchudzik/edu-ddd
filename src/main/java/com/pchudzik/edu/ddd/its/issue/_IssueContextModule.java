package com.pchudzik.edu.ddd.its.issue;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainService;

public class _IssueContextModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IssueCreation.class).annotatedWith(DomainService.class).to(IssueCreationImpl.class).in(Singleton.class);
        bind(IssueCreation.class).to(IssueCreationAppService.class).in(Singleton.class);
        bind(IssueRepository.class).in(Singleton.class);
    }
}

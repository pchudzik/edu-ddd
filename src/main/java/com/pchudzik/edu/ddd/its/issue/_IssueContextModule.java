package com.pchudzik.edu.ddd.its.issue;

import com.google.inject.AbstractModule;
import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainService;

public class _IssueContextModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IssueCreation.class).annotatedWith(DomainService.class).to(IssueCreationImpl.class);
        bind(IssueCreation.class).to(IssueCreationAppService.class);
        bind(IssueRepository.class);
    }
}

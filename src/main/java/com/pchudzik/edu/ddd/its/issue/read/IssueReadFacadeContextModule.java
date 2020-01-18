package com.pchudzik.edu.ddd.its.issue.read;

import com.google.inject.AbstractModule;

public class IssueReadFacadeContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IssueReadFacade.class).to(IssueReadFacadeImpl.class);
    }
}

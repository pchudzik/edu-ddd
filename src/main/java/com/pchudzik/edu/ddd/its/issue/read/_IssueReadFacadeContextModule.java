package com.pchudzik.edu.ddd.its.issue.read;

import com.google.inject.AbstractModule;

public class _IssueReadFacadeContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IssueRead.class).to(IssueReadImpl.class);
    }
}

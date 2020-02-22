package com.pchudzik.edu.ddd.its.issue.read;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class _IssueReadFacadeContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IssueRead.class).to(IssueReadImpl.class).in(Singleton.class);
    }
}

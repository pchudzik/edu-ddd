package com.pchudzik.edu.ddd.its.infrastructure;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.pchudzik.edu.ddd.its.infrastructure.db.DatabaseContextModule;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessagingContextModule;
import com.pchudzik.edu.ddd.its.issue.IssueContextModule;
import com.pchudzik.edu.ddd.its.issue.id.IssueIdContextModule;
import com.pchudzik.edu.ddd.its.issue.read.IssueReadFacadeContextModule;
import com.pchudzik.edu.ddd.its.project.ProjectContextModule;
import com.pchudzik.edu.ddd.its.project.read.ProjectReadContextModule;

public class InjectorFactory {
    public static Injector injector() {
        return Guice.createInjector(
                new DatabaseContextModule(),
                new MessagingContextModule(),
                new ProjectContextModule(),
                new ProjectReadContextModule(),
                new IssueIdContextModule(),
                new IssueReadFacadeContextModule(),
                new IssueContextModule()
        );
    }
}
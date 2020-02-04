package com.pchudzik.edu.ddd.its.infrastructure;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.pchudzik.edu.ddd.its.field.FieldContextModule;
import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitionsContextModule;
import com.pchudzik.edu.ddd.its.field.read.FieldReadContextModule;
import com.pchudzik.edu.ddd.its.infrastructure.db.DatabaseContextModule;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessagingContextModule;
import com.pchudzik.edu.ddd.its.issue.IssueContextModule;
import com.pchudzik.edu.ddd.its.issue.id.IssueIdContextModule;
import com.pchudzik.edu.ddd.its.issue.read.IssueReadFacadeContextModule;
import com.pchudzik.edu.ddd.its.project.ProjectContextModule;
import com.pchudzik.edu.ddd.its.project.read.ProjectReadContextModule;

public class InjectorFactory {
    private static Injector injector;

    public synchronized static Injector injector() {
        if (injector == null) {
            injector = Guice.createInjector(
                    new DatabaseContextModule(),
                    new MessagingContextModule(),
                    new ProjectContextModule(),
                    new ProjectReadContextModule(),
                    new IssueIdContextModule(),
                    new IssueReadFacadeContextModule(),
                    new IssueContextModule(),
                    new FieldContextModule(),
                    new FieldReadContextModule(),
                    new FieldDefinitionsContextModule()
            );
        }
        return injector;
    }
}

package com.pchudzik.edu.ddd.its.infrastructure;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.pchudzik.edu.ddd.its.field._FieldContextModule;
import com.pchudzik.edu.ddd.its.field.defaults.assignment._FieldDefinitionsContextModule;
import com.pchudzik.edu.ddd.its.field.read._FieldReadContextModule;
import com.pchudzik.edu.ddd.its.infrastructure.db._DatabaseContextModule;
import com.pchudzik.edu.ddd.its.infrastructure.queue._MessagingContextModule;
import com.pchudzik.edu.ddd.its.issue._IssueContextModule;
import com.pchudzik.edu.ddd.its.issue.id._IssueIdContextModule;
import com.pchudzik.edu.ddd.its.issue.read._IssueReadFacadeContextModule;
import com.pchudzik.edu.ddd.its.project._ProjectContextModule;
import com.pchudzik.edu.ddd.its.project.read._ProjectReadContextModule;

public class InjectorFactory {
    private static Injector injector;

    public synchronized static Injector injector(Module securityModule) {
        if (injector == null) {
            injector = Guice.createInjector(
                    securityModule,
                    new _DatabaseContextModule(),
                    new _MessagingContextModule(),
                    new _ProjectContextModule(),
                    new _ProjectReadContextModule(),
                    new _IssueIdContextModule(),
                    new _IssueReadFacadeContextModule(),
                    new _IssueContextModule(),
                    new _FieldContextModule(),
                    new _FieldReadContextModule(),
                    new _FieldDefinitionsContextModule()
            );
        }
        return injector;
    }

    public synchronized static Injector injector() {
        if (injector == null) {
            injector = Guice.createInjector(
                    new _DatabaseContextModule(),
                    new _MessagingContextModule(),
                    new _ProjectContextModule(),
                    new _ProjectReadContextModule(),
                    new _IssueIdContextModule(),
                    new _IssueReadFacadeContextModule(),
                    new _IssueContextModule(),
                    new _FieldContextModule(),
                    new _FieldReadContextModule(),
                    new _FieldDefinitionsContextModule()
            );
        }
        return injector;
    }
}

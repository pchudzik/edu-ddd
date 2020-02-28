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
import com.pchudzik.edu.ddd.its.user.access._AccessContextModule;

import java.util.HashMap;
import java.util.Map;

public class InjectorFactory {
    private static Injector injector;

    private static Map<Class<? extends Module>, Injector> cache = new HashMap<>();

    public synchronized static Injector injector(Module securityModule) {
        if (!cache.containsKey(securityModule.getClass())) {
            cache.put(securityModule.getClass(), Guice.createInjector(
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
                    new _FieldDefinitionsContextModule()));
        }

        return cache.get(securityModule.getClass());
    }

    public synchronized static Injector injector() {
        return injector(new _AccessContextModule());
    }
}

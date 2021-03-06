package com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.access

import com.google.inject.AbstractModule
import com.pchudzik.edu.ddd.its.user.access.Access

class _VoidAccessContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Access).to(VoidAccess)
    }
}

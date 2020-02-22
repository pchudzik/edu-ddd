package com.pchudzik.edu.ddd.its.infrastructure.test.fixtures

import com.google.inject.Injector
import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.access._VoidAccessContextModule

class TestInjectorFactory {
    static Injector injector() {
        return InjectorFactory.injector(new _VoidAccessContextModule())
    }
}

package com.pchudzik.edu.ddd.its.infrastructure.db


import com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.TestInjectorFactory
import org.jdbi.v3.core.Jdbi
import spock.lang.Specification

class DbSpecification extends Specification {
    def injector = TestInjectorFactory.injector()

    void setup() {
        new DatabaseCleaner(injector.getInstance(Jdbi)).cleanupDb()
    }
}

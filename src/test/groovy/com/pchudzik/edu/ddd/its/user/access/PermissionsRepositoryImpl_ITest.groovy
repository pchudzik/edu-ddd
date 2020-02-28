package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.TestInjectorFactory

class PermissionsRepositoryImpl_ITest extends DbSpecification {
    def repository = TestInjectorFactory.realSecurityInjector().getInstance(PermissionsRepository)

    def x() {
        expect: repository != null
    }
}

package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.TestInjectorFactory
import com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.access.EmptyPrincipal
import com.pchudzik.edu.ddd.its.user.UserId
import com.pchudzik.edu.ddd.its.user.Users
import com.pchudzik.edu.ddd.its.user.Users.UserCreationCommand
import com.pchudzik.edu.ddd.its.user.Users.UserDeletionCommand

class UserPermissionsRepositoryImpl_ITest extends DbSpecification {
    def repository = TestInjectorFactory.realSecurityInjector().getInstance(UserPermissionsRepository)
    def users = TestInjectorFactory.injector().getInstance(Users)

    def "user permissions are not found for non existing user"() {
        expect:
        !repository.findOne(new UserId(UUID.randomUUID())).isPresent()
    }

    def "user permissions are not found for deleted user"() {
        given:
        def userId = users.createUser(UserCreationCommand.builder()
                .principal(new EmptyPrincipal())
                .login("login")
                .build())
        users.deleteUser(UserDeletionCommand.builder()
                .principal(new EmptyPrincipal())
                .userId(userId)
                .build())

        expect:
        !repository.findOne(new UserId(UUID.randomUUID())).isPresent()
    }

}

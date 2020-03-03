package com.pchudzik.edu.ddd.its.user

import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import org.jdbi.v3.core.Jdbi
import spock.lang.Unroll

class UserRepository_ITest extends DbSpecification {
    def repo = injector.getInstance(UserRepository)
    def jdbi = injector.getInstance(Jdbi)

    @Unroll
    def "saves new user"() {
        when:
        repo.save(user)

        then:
        repo.findOne(user.id).get().snapshot == user

        where:
        user << [
                new User("login", "nice name").snapshot,
                new User("only_login").snapshot
        ]
    }

    def "updates only display name of the user"() {
        given:
        def user = new User("only_login")
        repo.save(user.snapshot)

        and:
        user.updateDisplayName("nice display name")

        when:
        repo.save(user.snapshot)

        then:
        repo.findOne(user.id).get().snapshot == user.snapshot
    }

    def "deleted user can not be found"() {
        given:
        def user = new User("only_login")
        user.markAsDeleted()

        when:
        repo.save(user.snapshot)

        then:
        !repo.findOne(user.id).isPresent()
        def found = jdbi.withHandle({ h ->
            h
                    .select("select id from users")
                    .mapToMap()
                    .one()
        })
        found["id"] == user.id.value
    }
}

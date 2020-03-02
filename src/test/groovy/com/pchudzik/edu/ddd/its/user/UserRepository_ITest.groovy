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
        def saved = jdbi.withHandle({ h ->
            h
                    .select("select * from users")
                    .mapToMap()
                    .one()
        })
        saved["id"] == user.id.value
        saved["login"] == user.login
        saved["display_name"] == user.displayName

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
        def saved = jdbi.withHandle({ h ->
            h
                    .select("select * from users")
                    .mapToMap()
                    .one()
        })
        saved["id"] == user.id.value
        saved["display_name"] == "nice display name"
    }
}

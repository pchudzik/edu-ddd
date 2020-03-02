package com.pchudzik.edu.ddd.its.user;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class UserRepositoryImpl implements UserRepository {
    private final Jdbi jdbi;

    @Override
    public void save(User.UserSnapshot user) {
        jdbi.useHandle(h -> {
            var updated = h
                    .createUpdate("update users set display_name = :name where id = :id")
                    .bind("name", user.getDisplayName())
                    .bind("id", user.getId().getValue())
                    .execute();

            if (updated > 0) {
                return;
            }

            h
                    .createUpdate("insert into users(id, login, display_name) values (:id, :login, :name)")
                    .bind("id", user.getId().getValue())
                    .bind("login", user.getLogin())
                    .bind("name", user.getDisplayName())
                    .execute();
        });
    }

    @Override
    public Optional<User> findOne(UserId userId) {
        return jdbi.withHandle(h -> h
                .select("select id, login, display_name from users where id = :id")
                .bind("id", userId.getValue())
                .map((rs, ctx) -> new User(
                        new UserId(rs.getString("id")),
                        rs.getString("login"),
                        rs.getString("display_name")
                ))
                .findOne());
    }
}

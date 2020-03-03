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
                    .createUpdate("update users set display_name = :name, deleted = :deleted where id = :id")
                    .bind("name", user.getDisplayName())
                    .bind("id", user.getId().getValue())
                    .bind("deleted", user.isDeleted())
                    .execute();

            if (updated > 0) {
                return;
            }

            h
                    .createUpdate("insert into users(id, login, display_name, deleted) values (:id, :login, :name, :deleted)")
                    .bind("id", user.getId().getValue())
                    .bind("login", user.getLogin())
                    .bind("name", user.getDisplayName())
                    .bind("deleted", user.isDeleted())
                    .execute();
        });
    }

    @Override
    public Optional<User> findOne(UserId userId) {
        return jdbi.withHandle(h -> h
                .select("select id, login, display_name, deleted from users where id = :id and deleted = false")
                .bind("id", userId.getValue())
                .map((rs, ctx) -> new User(
                        new UserId(rs.getString("id")),
                        rs.getString("login"),
                        rs.getString("display_name"),
                        rs.getBoolean("deleted")
                ))
                .findOne());
    }
}

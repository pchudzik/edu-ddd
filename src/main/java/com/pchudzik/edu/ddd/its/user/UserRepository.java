package com.pchudzik.edu.ddd.its.user;

import com.pchudzik.edu.ddd.its.user.User.UserSnapshot;

import java.util.Optional;

interface UserRepository {
    void save(UserSnapshot user);

    Optional<User> findOne(UserId userId);
}

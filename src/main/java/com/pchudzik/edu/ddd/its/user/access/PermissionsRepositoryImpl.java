package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.user.UserId;

import java.util.Optional;

class PermissionsRepositoryImpl implements PermissionsRepository {
    @Override
    public Optional<UserPermissions> findOne(UserId userId) {
        return Optional.empty();
    }
}

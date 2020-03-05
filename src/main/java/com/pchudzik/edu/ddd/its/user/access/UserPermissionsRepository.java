package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.user.UserId;

import java.util.Optional;

interface UserPermissionsRepository {
    Optional<UserPermissions> findOne(UserId userId);
}

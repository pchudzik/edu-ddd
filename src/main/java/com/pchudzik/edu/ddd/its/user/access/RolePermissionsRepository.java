package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.user.UserId;

interface RolePermissionsRepository {
    void save(RolePermissions.RolePermissionsSnapshot snapshot);

    RolePermissions findOne(RoleId roleId);

    void assignRoleToUser(UserId userId, RoleId roleId);
}

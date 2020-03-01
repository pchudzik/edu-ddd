package com.pchudzik.edu.ddd.its.user.access;

interface RolePermissionsRepository {
    void save(RolePermissions.RolePermissionsSnapshot snapshot);

    RolePermissions findOne(RoleId roleId);
}

package com.pchudzik.edu.ddd.its.user.access

import com.pchudzik.edu.ddd.its.user.UserId

class AccessFixtures {
    static UserPermissions user(Permission... permissions) {
        new UserPermissions(
                new UserId(),
                new ApplicablePermissions(Arrays.asList(permissions)))
    }
}

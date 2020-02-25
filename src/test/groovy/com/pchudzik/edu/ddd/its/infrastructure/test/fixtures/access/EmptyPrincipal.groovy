package com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.access

import com.pchudzik.edu.ddd.its.user.UserId
import com.pchudzik.edu.ddd.its.user.access.Principal

class EmptyPrincipal implements Principal {
    private static final def userId = new UserId(UUID.fromString("00000000-0000-0000-0000-000000000000"))

    @Override
    UserId getUserId() {
        return userId
    }
}

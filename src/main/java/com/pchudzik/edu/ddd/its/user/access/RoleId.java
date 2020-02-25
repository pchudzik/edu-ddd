package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.infrastructure.domain.Id;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
class RoleId implements Id {
    private final UUID id;

    public RoleId() {
        this(UUID.randomUUID());
    }
}

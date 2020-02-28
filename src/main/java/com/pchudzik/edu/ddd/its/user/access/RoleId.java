package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.infrastructure.domain.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
class RoleId implements Id {
    @Getter
    private final UUID value;

    public RoleId() {
        this(UUID.randomUUID());
    }
}

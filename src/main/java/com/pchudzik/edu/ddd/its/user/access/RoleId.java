package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.infrastructure.domain.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode
@RequiredArgsConstructor
public class RoleId implements Id {
    @Getter
    private final UUID value;

    public RoleId(String id) {
        this(UUID.fromString(id));
    }

    public RoleId() {
        this(UUID.randomUUID());
    }
}

package com.pchudzik.edu.ddd.its.user;

import com.pchudzik.edu.ddd.its.infrastructure.domain.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@EqualsAndHashCode
public class UserId implements Id {
    @Getter
    private final UUID value;

    public UserId() {
        this(UUID.randomUUID());
    }

    public UserId(UUID value) {
        this.value = value;
    }

    public UserId(String id) {
        this(UUID.fromString(id));
    }
}

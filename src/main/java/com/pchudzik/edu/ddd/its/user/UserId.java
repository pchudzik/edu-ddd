package com.pchudzik.edu.ddd.its.user;

import java.util.UUID;

public class UserId {
    private final UUID id;

    public UserId() {
        this(UUID.randomUUID());
    }

    public UserId(UUID id) {
        this.id = id;
    }
}

package com.pchudzik.edu.ddd.its.issue;

import java.util.UUID;

class IssueId {
    private final UUID id;

    public IssueId() {
        this(UUID.randomUUID());
    }

    public IssueId(UUID id) {
        this.id = id;
    }
}

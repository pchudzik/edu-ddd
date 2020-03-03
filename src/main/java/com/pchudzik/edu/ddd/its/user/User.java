package com.pchudzik.edu.ddd.its.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
class User {
    @Getter
    private final UserId id;
    private final String login;
    private String displayName;
    private boolean deleted;

    public User(String login) {
        this(new UserId(), login);
    }

    public User(UserId userId, String login) {
        this.id = userId;
        this.login = login;
    }

    public User(String login, String displayName) {
        this.id = new UserId();
        this.login = login;
        this.displayName = displayName;
    }

    public void updateDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserSnapshot getSnapshot() {
        return new UserSnapshot(id, login, displayName, deleted);
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class UserSnapshot {
        private final UserId id;
        private final String login;
        private final String displayName;
        private final boolean deleted;
    }
}

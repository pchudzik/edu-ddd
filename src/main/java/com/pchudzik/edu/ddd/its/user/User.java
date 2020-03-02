package com.pchudzik.edu.ddd.its.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

class User {
    @Getter
    private final UserId id;
    private final String login;
    private String displayName;

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

    public User(UserId id, String login, String displayName) {
        this.id = id;
        this.login = login;
        this.displayName = displayName;
    }

    public void updateDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserSnapshot getSnapshot() {
        return new UserSnapshot(id, login, displayName);
    }

    @Getter
    @RequiredArgsConstructor
    static class UserSnapshot {
        private final UserId id;
        private final String login;
        private final String displayName;
    }
}

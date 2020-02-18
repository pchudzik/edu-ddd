package com.pchudzik.edu.ddd.its.user;

class User {
    private final UserId userId;
    private final UserDisplayDetails userDisplayDetails;

    public User(String login) {
        this(new UserId(), login);
    }

    public User(UserId userId, String login) {
        this.userId = userId;
        this.userDisplayDetails = new UserDisplayDetails(login);
    }

    private class UserDisplayDetails {
        private String login;
        private String displayName;

        public UserDisplayDetails(String login) {
            this.login = login;
        }
    }
}

package com.pchudzik.edu.ddd.its.user;

class User {
    UserId userId;

    UserDisplayDetails userDisplayDetails;

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

package com.pchudzik.edu.ddd.its.user;

import com.pchudzik.edu.ddd.its.user.access.AuthorizedCommand;
import com.pchudzik.edu.ddd.its.user.access.Principal;
import lombok.Builder;
import lombok.Getter;

public interface Users {
    UserId createUser(UserCreationCommand cmd);

    void updateUser(UserUpdateCommand cmd);

    void deleteUser(UserDeletionCommand userId);

    @Getter
    @Builder
    class UserCreationCommand implements AuthorizedCommand {
        private final String login;
        private final String displayName;
        private final Principal principal;
    }

    @Getter
    @Builder
    class UserUpdateCommand implements AuthorizedCommand {
        private final UserId userId;
        private final String displayName;
        private final Principal principal;
    }

    @Getter
    @Builder
    class UserDeletionCommand {
        private final UserId userId;
        private final Principal principal;
    }
}

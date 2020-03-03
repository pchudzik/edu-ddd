package com.pchudzik.edu.ddd.its.user;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.domain.NoSuchObjectException;
import com.pchudzik.edu.ddd.its.user.access.Access;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UsersImpl implements Users {
    private final TransactionManager txManager;
    private final Access access;
    private final UserRepository userRepository;

    @Override
    public UserId createUser(UserCreationCommand cmd) {
        return txManager.inTransaction(() -> access.ifCanManageUsers(
                cmd.getPrincipal(),
                () -> {
                    var user = new User(cmd.getLogin(), cmd.getDisplayName());
                    userRepository.save(user.getSnapshot());
                    return user.getId();
                }
        ));
    }

    @Override
    public void updateUser(UserUpdateCommand cmd) {
        txManager.useTransaction(() -> access.ifCanManageUser(
                cmd.getPrincipal(), cmd.getUserId(),
                () -> {
                    var user = findUser(cmd.getUserId());
                    user.updateDisplayName(cmd.getDisplayName());
                    userRepository.save(user.getSnapshot());
                }));
    }

    @Override
    public void deleteUser(UserDeletionCommand cmd) {
        txManager.useTransaction(() -> access.ifCanManageUsers(
                cmd.getPrincipal(),
                () -> {
                    var user = findUser(cmd.getUserId());
                    user.markAsDeleted();
                    userRepository.save(user.getSnapshot());
                    return null;
                }));
    }

    private User findUser(UserId userId) {
        return userRepository
                .findOne(userId)
                .orElseThrow(() -> new NoSuchObjectException(userId));
    }
}

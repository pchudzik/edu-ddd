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
                    var user = userRepository
                            .findOne(cmd.getUserId())
                            .orElseThrow(() -> new NoSuchObjectException(cmd.getUserId()));
                    user.updateDisplayName(cmd.getDisplayName());
                    userRepository.save(user.getSnapshot());
                }));
    }

    @Override
    public void deleteUser(UserDeletionCommand userId) {

    }
}

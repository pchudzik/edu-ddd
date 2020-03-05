package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.UserId;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class UserPermissionsRepositoryImpl implements UserPermissionsRepository {
    private final Jdbi jdbi;

    @Override
    public Optional<UserPermissions> findOne(UserId userId) {
        Optional<List<Permission>> permissions = jdbi.withHandle(h -> {
            if (!isUserActive(userId, h)) {
                return Optional.empty();
            }

            return Optional.ofNullable(h
                    .select("" +
                            "select type, project " +
                            "from permissions permission " +
                            "left join user_roles roles on role.user_id = :userId " +
                            "join user user on user.id = :userId " +
                            "where permission.owner_id = :userId " +
                            "or permission.owner_id = roles.role_id")
                    .bind("userId", userId.getValue())
                    .map(new UserPermissionsMapper())
                    .list());
        });

        return permissions
                .map(ApplicablePermissions::new)
                .map(applicablePermissions -> new UserPermissions(userId, applicablePermissions));
    }

    private boolean isUserActive(UserId userId, Handle h) {
        return h
                .select("select count(id) from users where id = :userId and deleted = false")
                .bind("userId", userId.getValue())
                .map((rs, cts) -> rs.getInt(1))
                .one() > 0;
    }

    private static class UserPermissionsMapper implements RowMapper<Permission> {

        @Override
        public Permission map(ResultSet rs, StatementContext ctx) throws SQLException {
            var permissionType = PermissionType.valueOf(rs.getString("type"));
            var project = Optional.ofNullable(rs.getString("project"))
                    .map(ProjectId::new)
                    .orElse(null);
            return PermissionFactory.createPermission(permissionType, project);
        }
    }

}

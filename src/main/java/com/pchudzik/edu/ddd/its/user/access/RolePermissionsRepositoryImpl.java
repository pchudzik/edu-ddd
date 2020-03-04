package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.UserId;
import com.pchudzik.edu.ddd.its.user.access.RolePermissions.RolePermissionsSnapshot;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class RolePermissionsRepositoryImpl implements RolePermissionsRepository {
    private final Jdbi jdbi;

    @Override
    public void save(RolePermissionsSnapshot snapshot) {
        jdbi.useHandle(h -> {
            deletePermissions(snapshot.getId());

            var isUpdated = executeUpdate(snapshot, h);
            if (!isUpdated) {
                executeSave(snapshot, h);
            }

            insertPermissions(snapshot, h);
        });
    }

    @Override
    public RolePermissions findOne(RoleId roleId) {
        return jdbi.withHandle(h -> {
            var permissions = h
                    .select("select type, project from permissions where owner_id = :roleId")
                    .bind("roleId", roleId.getValue())
                    .map(new PermissionRowMapper())
                    .list();
            return h
                    .select("select id, name from roles where id = :roleId")
                    .bind("roleId", roleId.getValue())
                    .map(new RolePermissionsMapper(permissions))
                    .one();
        });
    }

    @Override
    public void assignRoleToUser(UserId userId, RoleId roleId) {
        jdbi.useHandle(h -> h
                .createUpdate("insert into user_roles(role_id, user_id) values(:roleId, :userId)")
                .bind("roleId", roleId.getValue())
                .bind("userId", userId.getValue())
                .execute());
    }

    private void deletePermissions(RoleId roleId) {
        jdbi.useHandle(h -> h
                .createUpdate("delete from permissions where owner_id = :roleId")
                .bind("roleId", roleId.getValue())
                .execute());
    }

    private boolean executeUpdate(RolePermissionsSnapshot snapshot, Handle handle) {
        var updatedRows = handle
                .createUpdate("update roles set name = :name where id = :roleId")
                .bind("name", snapshot.getName())
                .bind("roleId", snapshot.getId().getValue())
                .execute();
        return updatedRows > 0;
    }

    private void executeSave(RolePermissionsSnapshot snapshot, Handle handle) {
        handle
                .createUpdate("insert into roles(id, name) values(:id, :name)")
                .bind("name", snapshot.getName())
                .bind("id", snapshot.getId().getValue())
                .execute();
    }

    private void insertPermissions(RolePermissionsSnapshot snapshot, Handle handle) {
        var projectAwareBatch = handle.prepareBatch("insert into permissions(id, owner_id, type, project) values(:id, :owner, :type, :project)");
        var generalPermissionBatch = handle.prepareBatch("insert into permissions(id, owner_id, type) values(:id, :owner, :type)");
        snapshot.getPermissions()
                .forEach(p -> {
                    var isProjectLevel = p.getPermissionType().isProjectLevel();
                    var batch = isProjectLevel ? projectAwareBatch : generalPermissionBatch;

                    batch
                            .bind("id", UUID.randomUUID())
                            .bind("owner", snapshot.getId().getValue())
                            .bind("type", p.getPermissionType());
                    p.getProjectId().ifPresent(pId -> batch.bind("project", pId.getValue()));

                    batch.add();
                });
        projectAwareBatch.execute();
        generalPermissionBatch.execute();
    }

    private static class PermissionRowMapper implements RowMapper<Permission> {
        @Override
        public Permission map(ResultSet rs, StatementContext ctx) throws SQLException {
            var permissionType = PermissionType.valueOf(rs.getString("type"));
            ProjectId projectId = null;
            if (permissionType.isProjectLevel()) {
                projectId = new ProjectId(rs.getString("project"));
            }
            return PermissionFactory.createPermission(permissionType, projectId);
        }
    }

    @RequiredArgsConstructor
    private static class RolePermissionsMapper implements RowMapper<RolePermissions> {
        private final Collection<Permission> permissions;

        @Override
        public RolePermissions map(ResultSet rs, StatementContext ctx) throws SQLException {
            var roleId = new RoleId(rs.getString("id"));
            var name = rs.getString("name");
            return new RolePermissions(roleId, name, permissions);
        }
    }
}

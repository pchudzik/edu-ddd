package com.pchudzik.edu.ddd.its.user.access;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class RolePermissionsRepositoryImpl implements RolePermissionsRepository {
    private final Jdbi jdbi;

    @Override
    public void save(RolePermissions.RolePermissionsSnapshot snapshot) {
        jdbi.useHandle(h -> {
            h
                    .createUpdate("insert into roles(id, name) values(:id, :name)")
                    .bind("name", snapshot.getName())
                    .bind("id", snapshot.getId().getValue())
                    .execute();

            var projectAwareBatch = h.prepareBatch("insert into permissions(id, owner_id, type, project) values(:id, :owner, :type, :project)");
            var generalPermissionBatch = h.prepareBatch("insert into permissions(id, owner_id, type) values(:id, :owner, :type)");
            snapshot.getPermissions()
                    .forEach(p -> {
                        var isProjectLevel = p.getPermissionType().isProjectLevel();
                        var batch = isProjectLevel ? projectAwareBatch : generalPermissionBatch;
                        batch
                                .bind("id", UUID.randomUUID())
                                .bind("owner", snapshot.getId().getValue())
                                .bind("type", p.getPermissionType());
                        p.getProjectId().ifPresent(pId -> batch.bind("project", pId.getValue()));
                    });
            projectAwareBatch.execute();
            generalPermissionBatch.execute();
        });
    }
}

package com.pchudzik.edu.ddd.its.project;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainService;
import com.pchudzik.edu.ddd.its.user.access.Access;

import javax.inject.Inject;

class ProjectCreationAppService implements ProjectCreation {
    private final TransactionManager txManager;
    private final Access access;
    private final ProjectCreation projectCreation;

    @Inject
    public ProjectCreationAppService(TransactionManager txManager, Access access, @DomainService ProjectCreation projectCreation) {
        this.txManager = txManager;
        this.access = access;
        this.projectCreation = projectCreation;
    }

    @Override
    public ProjectId createNewProject(ProjectCreationCommand creationCommand) {
        return txManager.inTransaction(() -> access.ifCanCreateProject(
                creationCommand.getPrincipal(),
                () -> projectCreation.createNewProject(creationCommand)));
    }

    @Override
    public void updateProject(ProjectId projectId, ProjectUpdateCommand updateCommand) {
        txManager.useTransaction(() -> access.ifCanUpdateProject(
                updateCommand.getPrincipal(),
                projectId,
                () -> projectCreation.updateProject(projectId, updateCommand)));
    }
}

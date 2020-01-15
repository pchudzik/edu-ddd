package com.pchudzik.edu.ddd.its.project;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class ProjectFacadeImpl implements ProjectFacade {
    private final ProjectRepository projectRepository;
    private final TransactionManager txManager;

    @Override
    public void createNewProject(ProjectCreationCommand creationCommand) {
        txManager.useTransaction(() -> {
            Project project = new Project(creationCommand.getId(), creationCommand.getName());
            project.projectDescription(creationCommand.getDescription());
            projectRepository.save(project);
        });
    }
}

package com.pchudzik.edu.ddd.its.project;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class ProjectCreationImpl implements ProjectCreation {
    private final ProjectRepository projectRepository;
    private final MessageQueue messageQueue;
    private final TransactionManager txManager;

    @Override
    public ProjectId createNewProject(ProjectCreationCommand creationCommand) {
        return txManager.inTransaction(() -> {
            Project project = new Project(creationCommand.getId(), creationCommand.getName());
            project.projectDescription(creationCommand.getDescription());
            projectRepository.save(project);
            messageQueue.publish(new ProjectCreatedMessage(project.getId()));
            return project.getId();
        });
    }
}

package com.pchudzik.edu.ddd.its.project;

import com.pchudzik.edu.ddd.its.field.FieldAssignment;
import com.pchudzik.edu.ddd.its.field.FieldAssignmentCommandFactory;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class ProjectCreationImpl implements ProjectCreation {
    private final ProjectRepository projectRepository;
    private final MessageQueue messageQueue;

    private final FieldAssignment fieldAssignment;

    @Override
    public ProjectId createNewProject(ProjectCreationCommand creationCommand) {
        Project project = new Project(creationCommand.getId(), creationCommand.getName());
        project.projectDescription(creationCommand.getDescription());
        projectRepository.save(project);

        fieldAssignment
                .assignFieldValues(
                        project.getId(),
                        creationCommand.getFieldAssignments().stream()
                                .map(FieldAssignmentCommandFactory::buildAssignmentCommand)
                                .collect(Collectors.toList()));


        messageQueue.publish(new ProjectCreatedMessage(project.getId()));
        return project.getId();
    }

    @Override
    public void updateProject(ProjectId projectId, ProjectUpdateCommand updateCommand) {
        var project = projectRepository.findOne(projectId);
        project.projectDescription(updateCommand.getDescription());
        project.projectName(updateCommand.getName());
        projectRepository.save(project);

        fieldAssignment.assignFieldValues(
                projectId,
                updateCommand.getFieldAssignments().stream()
                        .map(FieldAssignmentCommandFactory::buildAssignmentCommand)
                        .collect(Collectors.toList()));
    }
}

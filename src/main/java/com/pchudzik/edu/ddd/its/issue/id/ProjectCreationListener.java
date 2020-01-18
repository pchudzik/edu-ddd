package com.pchudzik.edu.ddd.its.issue.id;

import com.google.common.eventbus.Subscribe;
import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import com.pchudzik.edu.ddd.its.project.ProjectFacade;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class ProjectCreationListener implements MessageQueue.MessageListener {
    private final TransactionManager txManger;
    private final IssueIdGeneratorRepository issueIdGeneratorRepository;

    @Subscribe
    public void onProjectCreation(ProjectFacade.ProjectCreatedMessage newProject) {
        txManger.useTransaction(() -> issueIdGeneratorRepository.createGenerator(newProject.getProjectId()));
    }
}
package com.pchudzik.edu.ddd.its.project.read;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.user.access.Access;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class ProjectViewImpl implements ProjectView {
    private final TransactionManager txManger;
    private final Access access;
    private final ProjectViewRepository projectViewRepository;

    @Override
    public List<ProjectDto> listProjects(ListProjectsCmd listCmd) {
        var accessToProjectPredicate = access.canViewProject(listCmd.getPrincipal());
        return txManger.inTransaction(() -> projectViewRepository
                .listProjects()
                .stream()
                .filter(p -> accessToProjectPredicate.test(p.getId()))
                .collect(Collectors.toList()));
    }
}

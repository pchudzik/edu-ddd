package com.pchudzik.edu.ddd.its.project.read;

import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class ProjectViewFacadeImpl implements ProjectViewFacade {
    private final ProjectViewRepository projectViewRepository;

    @Override
    public List<ProjectDto> listProjects() {
        return projectViewRepository.listProjects();
    }
}

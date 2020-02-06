package com.pchudzik.edu.ddd.its.project.read;

import java.util.List;

interface ProjectViewRepository {
    List<ProjectView.ProjectDto> listProjects();
}

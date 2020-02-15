package com.pchudzik.edu.ddd.its.project;

interface ProjectRepository {
    void save(Project project);

    Project findOne(ProjectId projectId);
}

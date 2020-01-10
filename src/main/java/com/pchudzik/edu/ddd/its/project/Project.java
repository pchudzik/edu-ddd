package com.pchudzik.edu.ddd.its.project;

import com.pchudzik.edu.ddd.its.infrastructure.ValidationException;
import org.apache.commons.lang3.StringUtils;

class Project {
    private ProjectId id;
    private String projectFullName;
    private String projectDescription;

    Project() {
    }

    Project(String projectId, String projectName) {
        this.id = new ProjectId(projectId);
        projectName(projectName);
    }

    public void projectName(String newProjectName) {
        if (StringUtils.isBlank(projectFullName)) {
            throw new ValidationException.EmptyValueValidationException("Project name can not be empty");
        }
        this.projectFullName = newProjectName;
    }

    public void projectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
}

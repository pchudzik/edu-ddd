package com.pchudzik.edu.ddd.its.project;

import com.pchudzik.edu.ddd.its.infrastructure.domain.ValidationException;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

class Project {
    @Getter(AccessLevel.PACKAGE)
    private ProjectId id;

    @Getter(AccessLevel.PACKAGE)
    private String projectFullName;

    @Getter(AccessLevel.PACKAGE)
    private String projectDescription;

    Project() {
    }

    Project(ProjectId projectId, String projectName) {
        this(projectId, projectName, null);
    }

    Project(ProjectId projectId, String projectName, String description) {
        this.id = projectId;
        projectName(projectName);
        projectDescription(description);
    }

    public void projectName(String newProjectName) {
        if (StringUtils.isBlank(newProjectName)) {
            throw new ValidationException.EmptyValueValidationException("Project name can not be empty");
        }
        this.projectFullName = newProjectName;
    }

    public void projectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
}

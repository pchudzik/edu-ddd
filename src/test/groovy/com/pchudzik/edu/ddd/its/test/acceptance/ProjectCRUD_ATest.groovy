package com.pchudzik.edu.ddd.its.test.acceptance


import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.project.ProjectCreation
import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.project.read.ProjectView

class ProjectCRUD_ATest extends DbSpecification {
    def projectFacade = injector.getInstance(ProjectCreation)
    def projectViewFacade = injector.getInstance(ProjectView)

    def "new project is created"() {
        given:
        def projectId = new ProjectId("ABCD")

        when:
        projectFacade.createNewProject(ProjectCreation.ProjectCreationCommand.builder()
                .id(projectId)
                .name("Some Project")
                .description("Some description")
                .build())

        then:
        def allProjects = projectViewFacade.listProjects()

        and:
        allProjects.size() == 1
        allProjects.contains(ProjectView.ProjectDto.builder()
                .id(projectId)
                .name("Some Project")
                .description("Some description")
                .build())
    }
}

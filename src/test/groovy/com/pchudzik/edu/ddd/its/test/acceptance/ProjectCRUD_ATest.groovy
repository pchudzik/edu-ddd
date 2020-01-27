package com.pchudzik.edu.ddd.its.test.acceptance


import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.project.ProjectFacade
import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.project.read.ProjectViewFacade

class ProjectCRUD_ATest extends DbSpecification {
    def projectFacade = injector.getInstance(ProjectFacade)
    def projectViewFacade = injector.getInstance(ProjectViewFacade)

    def "new project is created"() {
        given:
        def projectId = new ProjectId("ABCD")

        when:
        projectFacade.createNewProject(ProjectFacade.ProjectCreationCommand.builder()
                .id(projectId)
                .name("Some Project")
                .description("Some description")
                .build())

        then:
        def allProjects = projectViewFacade.listProjects()

        and:
        allProjects.size() == 1
        allProjects.contains(ProjectViewFacade.ProjectDto.builder()
                .id(projectId)
                .name("Some Project")
                .description("Some description")
                .build())
    }
}

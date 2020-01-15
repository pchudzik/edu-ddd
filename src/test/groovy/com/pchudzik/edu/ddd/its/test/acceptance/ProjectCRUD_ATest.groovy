package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import com.pchudzik.edu.ddd.its.project.ProjectFacade

import com.pchudzik.edu.ddd.its.project.read.ProjectViewFacade
import spock.lang.Specification

class ProjectCRUD_ATest extends Specification {
    def injector = InjectorFactory.injector()

    def projectFacade = injector.getInstance(ProjectFacade)
    def projectViewFacade = injector.getInstance(ProjectViewFacade)

    def "new project is created"() {
        when:
        projectFacade.createNewProject(ProjectFacade.ProjectCreationCommand.builder()
                .id("ABCD")
                .name("Some Project")
                .description("Some description")
                .build())

        then:
        def allProjects = projectViewFacade.listProjects()

        and:
        allProjects.size() == 1
        allProjects.contains(ProjectViewFacade.ProjectDto.builder()
                .id("ABCD")
                .name("Some Project")
                .description("Some description")
                .build())
    }
}

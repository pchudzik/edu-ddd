package com.pchudzik.edu.ddd.its.project


import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue
import spock.lang.Specification

class ProjectFacadeTest extends Specification {
    def projectRepository = Mock(ProjectRepository)
    def queue = Mock(MessageQueue)
    def facade = new ProjectFacadeImpl(projectRepository, queue, new TransactionManager.NoTransactionManager())

    def "event is boradcasted after project is created"() {
        given:
        def id = new ProjectId("ABC")
        def project = ProjectFacade.ProjectCreationCommand.builder()
                .id(id)
                .name("asd")
                .build()

        when:
        facade.createNewProject(project)

        then:
        1 * queue.publish(new ProjectFacade.ProjectCreatedMessage(id))
    }
}

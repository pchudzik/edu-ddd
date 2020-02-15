package com.pchudzik.edu.ddd.its.project

import com.pchudzik.edu.ddd.its.field.FieldAssignment
import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager.NoTransactionManager
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue
import com.pchudzik.edu.ddd.its.project.ProjectCreation.ProjectCreatedMessage
import com.pchudzik.edu.ddd.its.project.ProjectCreation.ProjectCreationCommand
import spock.lang.Specification

class ProjectCreationTest extends Specification {
    def projectRepository = Mock(ProjectRepository)
    def queue = Mock(MessageQueue)
    def fieldAssignment = Mock(FieldAssignment)
    def facade = new ProjectCreationImpl(projectRepository, queue, new NoTransactionManager(), fieldAssignment)

    def "event is boradcasted after project is created"() {
        given:
        def id = new ProjectId("ABC")
        def project = ProjectCreationCommand.builder()
                .id(id)
                .name("asd")
                .build()

        when:
        facade.createNewProject(project)

        then:
        1 * queue.publish(new ProjectCreatedMessage(id))
    }
}

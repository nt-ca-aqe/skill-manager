package skillmanagement.domain.projects.usecases.add

import org.springframework.util.IdGenerator
import skillmanagement.common.events.PublishEvent
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.projects.model.Project
import skillmanagement.domain.projects.model.ProjectAddedEvent
import skillmanagement.domain.projects.model.ProjectDescription
import skillmanagement.domain.projects.model.ProjectLabel
import java.time.Clock

@BusinessFunction
class AddProject(
    private val idGenerator: IdGenerator,
    private val insertProjectIntoDataStore: InsertProjectIntoDataStore,
    private val publishEvent: PublishEvent,
    private val clock: Clock
) {

    // TODO: Security - Only invokable by Project-Admins
    operator fun invoke(label: ProjectLabel, description: ProjectDescription): Project {
        val project = Project(
            id = idGenerator.generateId(),
            version = 1,
            label = label,
            description = description,
            lastUpdate = clock.instant()
        )
        insertProjectIntoDataStore(project)
        publishEvent(ProjectAddedEvent(project))
        return project
    }

}

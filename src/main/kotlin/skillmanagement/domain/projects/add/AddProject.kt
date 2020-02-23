package skillmanagement.domain.projects.add

import org.springframework.util.IdGenerator
import skillmanagement.domain.projects.Project
import skillmanagement.domain.projects.ProjectDescription
import skillmanagement.domain.projects.ProjectLabel
import skillmanagement.domain.BusinessFunction

@BusinessFunction
class AddProject(
    private val idGenerator: IdGenerator,
    private val insertProjectIntoDataStore: InsertProjectIntoDataStore
) {

    // TODO: Security - Only invokable by Project-Admins
    operator fun invoke(label: ProjectLabel, description: ProjectDescription): Project {
        val project = Project(
            id = idGenerator.generateId(),
            label = label,
            description = description
        )
        insertProjectIntoDataStore(project)
        return project
    }

}

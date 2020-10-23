package skillmanagement.domain.projects.usecases.update

import skillmanagement.common.events.PublishEvent
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.projects.model.Project
import skillmanagement.domain.projects.model.ProjectUpdatedEvent
import skillmanagement.domain.projects.usecases.get.GetProjectById
import skillmanagement.domain.projects.usecases.update.UpdateProjectByIdResult.ProjectNotFound
import skillmanagement.domain.projects.usecases.update.UpdateProjectByIdResult.SuccessfullyUpdated
import java.util.UUID

@BusinessFunction
class UpdateProjectById(
    private val getProjectById: GetProjectById,
    private val updateProjectInDataStore: UpdateProjectInDataStore,
    private val publishEvent: PublishEvent
) {

    // TODO: Security - Who can change Projects?
    @RetryOnConcurrentProjectUpdate
    operator fun invoke(projectId: UUID, block: (Project) -> Project): UpdateProjectByIdResult {
        val currentProject = getProjectById(projectId) ?: return ProjectNotFound
        val modifiedProject = block(currentProject)

        assertNoInvalidModifications(currentProject, modifiedProject)

        val updatedProject = updateProjectInDataStore(modifiedProject)
        publishEvent(ProjectUpdatedEvent(updatedProject))
        return SuccessfullyUpdated(updatedProject)
    }

    private fun assertNoInvalidModifications(currentProject: Project, modifiedProject: Project) {
        check(currentProject.id == modifiedProject.id) { "ID must not be changed!" }
        check(currentProject.version == modifiedProject.version) { "Version must not be changed!" }
        check(currentProject.lastUpdate == modifiedProject.lastUpdate) { "Last update must not be changed!" }
    }

}

sealed class UpdateProjectByIdResult {
    object ProjectNotFound : UpdateProjectByIdResult()
    data class SuccessfullyUpdated(val project: Project) : UpdateProjectByIdResult()
}

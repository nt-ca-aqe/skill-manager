package skillmanagement.domain.projects.usecases.update

import com.github.fge.jsonpatch.JsonPatch
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import skillmanagement.common.ApplyPatch
import skillmanagement.domain.HttpAdapter
import skillmanagement.domain.projects.model.Project
import skillmanagement.domain.projects.model.ProjectDescription
import skillmanagement.domain.projects.model.ProjectLabel
import skillmanagement.domain.projects.model.ProjectResource
import skillmanagement.domain.projects.model.toResource
import skillmanagement.domain.projects.usecases.update.UpdateProjectByIdResult.ProjectNotFound
import skillmanagement.domain.projects.usecases.update.UpdateProjectByIdResult.SuccessfullyUpdated
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/projects/{projectId}")
class UpdateProjectByIdHttpAdapter(
    private val updateProjectById: UpdateProjectById,
    private val applyPatch: ApplyPatch
) {

    @PutMapping
    fun put(
        @PathVariable projectId: UUID,
        @RequestBody request: ChangeData
    ): ResponseEntity<ProjectResource> {
        val result = updateProjectById(projectId) {
            it.merge(request)
        }
        return when (result) {
            ProjectNotFound -> notFound().build()
            is SuccessfullyUpdated -> ok(result.project.toResource())
        }
    }

    @PatchMapping(consumes = ["application/json-patch+json"])
    fun patch(
        @PathVariable projectId: UUID,
        @RequestBody patch: JsonPatch
    ): ResponseEntity<ProjectResource> {
        val result = updateProjectById(projectId) {
            it.merge(applyPatch(patch, it.toChangeData()))
        }
        return when (result) {
            ProjectNotFound -> notFound().build()
            is SuccessfullyUpdated -> ok(result.project.toResource())
        }
    }

    data class ChangeData(
        val label: ProjectLabel,
        val description: ProjectDescription
    )

    private fun Project.toChangeData(): ChangeData =
        ChangeData(label = label, description = description)

    private fun Project.merge(changes: ChangeData): Project =
        copy(label = changes.label, description = changes.description)

}
package skillmanagement.domain.projects.usecases.get

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.projects.model.ProjectResource
import skillmanagement.domain.projects.model.toResource
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/projects/{id}")
class GetProjectByIdHttpAdapter(
    private val getProjectById: GetProjectById
) {

    @GetMapping
    fun get(@PathVariable id: UUID): ResponseEntity<ProjectResource> {
        val project = getProjectById(id)
        if (project != null) {
            return ok(project.toResource())
        }
        return noContent().build()
    }

}

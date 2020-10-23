package skillmanagement.domain.projects.usecases.add

import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.projects.model.ProjectDescription
import skillmanagement.domain.projects.model.ProjectLabel
import skillmanagement.domain.projects.model.ProjectResource
import skillmanagement.domain.projects.model.toResource

@HttpAdapter
@RequestMapping("/api/projects")
class AddProjectHttpAdapter(
    private val addProject: AddProject
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun post(@RequestBody request: Request): ProjectResource {
        val project = addProject(
            label = request.label,
            description = request.description
        )
        return project.toResource()
    }

    data class Request(
        val label: ProjectLabel,
        val description: ProjectDescription
    )

}

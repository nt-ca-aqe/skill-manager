package skillmanagement.domain.projects.usecases.find

import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.projects.model.ProjectResource
import skillmanagement.domain.projects.model.toSearchResource

@HttpAdapter
@RequestMapping("/api/projects/_search")
class SearchForProjectsHttpAdapter(
    private val findProjects: FindProjects
) {

    @PostMapping
    fun post(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "100") size: Int,
        @RequestBody request: Request
    ): PagedModel<ProjectResource> {
        val projects = findProjects(ProjectsMatchingQuery(PageIndex(page), PageSize(size), request.query))
        return projects.toSearchResource()
    }

    data class Request(
        val query: String
    )

}

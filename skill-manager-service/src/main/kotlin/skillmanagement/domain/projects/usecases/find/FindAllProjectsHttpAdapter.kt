package skillmanagement.domain.projects.usecases.find

import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.projects.model.ProjectResource
import skillmanagement.domain.projects.model.toAllResource

@HttpAdapter
@RequestMapping("/api/projects")
class FindAllProjectsHttpAdapter(
    private val findProjects: FindProjects
) {

    @GetMapping
    fun get(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "100") size: Int
    ): PagedModel<ProjectResource> {
        val projects = findProjects(AllProjectsQuery(PageIndex(page), PageSize(size)))
        return projects.toAllResource()
    }

}

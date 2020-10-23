package skillmanagement.domain.projects

import org.springframework.hateoas.PagedModel
import skillmanagement.domain.projects.model.ProjectDescription
import skillmanagement.domain.projects.model.ProjectLabel
import skillmanagement.domain.projects.model.ProjectResource
import skillmanagement.domain.projects.usecases.add.AddProjectHttpAdapter
import skillmanagement.domain.projects.usecases.find.SearchForProjectsHttpAdapter
import skillmanagement.test.AbstractHttpTestDriver
import java.util.UUID

class ProjectsTestDriver(
    host: String = "localhost",
    port: Int = 8080
) : AbstractHttpTestDriver(host, port) {

    fun add(
        label: String = "Dummy Project",
        description: String = "Lorem Ipsum ..."
    ): ProjectResource {
        val response = post("/api/projects") {
            AddProjectHttpAdapter.Request(
                label = ProjectLabel(label),
                description = description.let(::ProjectDescription)
            )
        }
        return when (response.code) {
            201 -> response.readBodyAs(ProjectResource::class)
            else -> error(unmappedCase(response))
        }
    }

    fun get(id: UUID): ProjectResource? {
        val response = get("/api/projects/$id")
        return when (response.code) {
            200 -> response.readBodyAs(ProjectResource::class)
            204 -> null
            else -> error(unmappedCase(response))
        }
    }

    fun getAll(page: Int = 0, size: Int = 100): PagedModel<ProjectResource> {
        val response = get("/api/projects?page=$page&size=$size")
        return when (response.code) {
            200 -> response.readBodyAs(ProjectsPageModel::class)
            else -> error(unmappedCase(response))
        }
    }

    fun search(query: String, page: Int = 0, size: Int = 100): PagedModel<ProjectResource> {
        val response = post("/api/projects/_search?page=$page&size=$size") {
            SearchForProjectsHttpAdapter.Request(query)
        }
        return when (response.code) {
            200 -> response.readBodyAs(ProjectsPageModel::class)
            else -> error(unmappedCase(response))
        }
    }

    fun delete(id: UUID) {
        val response = delete("/api/projects/$id")
        if (response.code != 204) {
            error(unmappedCase(response))
        }
    }

    private class ProjectsPageModel : PagedModel<ProjectResource>()

}

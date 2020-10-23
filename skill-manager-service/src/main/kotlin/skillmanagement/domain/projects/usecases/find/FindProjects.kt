package skillmanagement.domain.projects.usecases.find

import skillmanagement.common.search.Page
import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.search.PagedFindAllQuery
import skillmanagement.common.search.PagedStringQuery
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.projects.model.Project
import skillmanagement.domain.projects.searchindex.ProjectSearchIndex
import skillmanagement.domain.projects.usecases.get.GetProjectsFromDataStore

@BusinessFunction
class FindProjects(
    private val getProjectsFromDataStore: GetProjectsFromDataStore,
    private val searchIndex: ProjectSearchIndex
) {

    // TODO: Security + query parameter + pagination
    operator fun invoke(query: FindProjectsQuery): Page<Project> {
        val page = when (query) {
            is ProjectsMatchingQuery -> searchIndex.query(query)
            is AllProjectsQuery -> searchIndex.findAll(query)
        }
        val projectsMap = getProjectsFromDataStore(page.content)
        val projects = page.content.mapNotNull { projectsMap[it] }
        return page.withOtherContent(projects)
    }

}

sealed class FindProjectsQuery

data class ProjectsMatchingQuery(
    override val pageIndex: PageIndex = PageIndex.DEFAULT,
    override val pageSize: PageSize = PageSize.DEFAULT,
    override val queryString: String
) : PagedStringQuery, FindProjectsQuery()

data class AllProjectsQuery(
    override val pageIndex: PageIndex = PageIndex.DEFAULT,
    override val pageSize: PageSize = PageSize.DEFAULT
) : PagedFindAllQuery, FindProjectsQuery()

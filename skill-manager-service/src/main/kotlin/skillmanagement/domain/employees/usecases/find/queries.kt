package skillmanagement.domain.employees.usecases.find

import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.search.PagedFindAllQuery
import skillmanagement.common.search.PagedStringQuery
import java.util.UUID

sealed class FindEmployeeQuery

data class EmployeesWithSkill(
    override val pageIndex: PageIndex = PageIndex.DEFAULT,
    override val pageSize: PageSize = PageSize.DEFAULT,
    val skillId: UUID
) : PagedStringQuery, FindEmployeeQuery() {
    override val queryString: String = "_skillIds:$skillId"
}

data class EmployeesWhoWorkedOnProject(
    override val pageIndex: PageIndex = PageIndex.DEFAULT,
    override val pageSize: PageSize = PageSize.DEFAULT,
    val projectId: UUID
) : PagedStringQuery, FindEmployeeQuery() {
    override val queryString: String = "_projectIds:$projectId"
}

data class EmployeesMatchingQuery(
    override val pageIndex: PageIndex = PageIndex.DEFAULT,
    override val pageSize: PageSize = PageSize.DEFAULT,
    override val queryString: String
) : PagedStringQuery, FindEmployeeQuery()

data class AllEmployeesQuery(
    override val pageIndex: PageIndex = PageIndex.DEFAULT,
    override val pageSize: PageSize = PageSize.DEFAULT
) : PagedFindAllQuery, FindEmployeeQuery()

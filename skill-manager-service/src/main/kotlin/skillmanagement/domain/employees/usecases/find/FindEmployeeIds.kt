package skillmanagement.domain.employees.usecases.find

import skillmanagement.common.search.Page
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.searchindex.EmployeeSearchIndex
import java.util.UUID

@BusinessFunction
class FindEmployeeIds(
    private val searchIndex: EmployeeSearchIndex
) {

    // TODO: Security + query parameter + pagination
    operator fun invoke(query: FindEmployeeQuery): Page<UUID> =
        when (query) {
            is EmployeesWithSkill -> searchIndex.query(query)
            is EmployeesWhoWorkedOnProject -> searchIndex.query(query)
            is EmployeesMatchingQuery -> searchIndex.query(query)
            is AllEmployeesQuery -> searchIndex.findAll(query)
        }

}

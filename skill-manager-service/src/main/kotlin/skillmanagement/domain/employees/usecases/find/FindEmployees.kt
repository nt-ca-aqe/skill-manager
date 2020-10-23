package skillmanagement.domain.employees.usecases.find

import skillmanagement.common.search.Page
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.searchindex.EmployeeSearchIndex
import skillmanagement.domain.employees.usecases.get.GetEmployeesFromDataStore

@BusinessFunction
class FindEmployees(
    private val getEmployeeFromDataStore: GetEmployeesFromDataStore,
    private val searchIndex: EmployeeSearchIndex
) {

    // TODO: Security + query parameter + pagination
    operator fun invoke(query: FindEmployeeQuery): Page<Employee> {
        val page = when (query) {
            is EmployeesWithSkill -> searchIndex.query(query)
            is EmployeesWhoWorkedOnProject -> searchIndex.query(query)
            is EmployeesMatchingQuery -> searchIndex.query(query)
            is AllEmployeesQuery -> searchIndex.findAll(query)
        }
        val employeesMap = getEmployeeFromDataStore(page.content)
        val employees = page.content.mapNotNull { employeesMap[it] }
        return page.withOtherContent(employees)
    }

}

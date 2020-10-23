package skillmanagement.domain.employees.usecases.get

import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.Employee
import java.util.UUID

@BusinessFunction
class GetEmployeeById(
    private val getEmployeeFromDataStore: GetEmployeesFromDataStore
) {

    // TODO: which kinds of users should be able to access this information? The employee + their managers?
    operator fun invoke(id: UUID): Employee? {
        return getEmployeeFromDataStore(id)
    }

}

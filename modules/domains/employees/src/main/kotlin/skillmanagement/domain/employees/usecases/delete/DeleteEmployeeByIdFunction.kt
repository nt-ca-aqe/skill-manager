package skillmanagement.domain.employees.usecases.delete

import skillmanagement.common.events.PublishEventFunction
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.EmployeeDeletedEvent
import skillmanagement.domain.employees.usecases.delete.DeleteEmployeeByIdResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.delete.DeleteEmployeeByIdResult.SuccessfullyDeleted
import skillmanagement.domain.employees.usecases.read.GetEmployeeByIdFunction
import java.util.UUID

@BusinessFunction
class DeleteEmployeeByIdFunction internal constructor(
    private val getEmployeeById: GetEmployeeByIdFunction,
    private val deleteEmployeeFromDataStore: DeleteEmployeeFromDataStoreFunction,
    private val publishEvent: PublishEventFunction
) {

    operator fun invoke(id: UUID): DeleteEmployeeByIdResult {
        val employee = getEmployeeById(id) ?: return EmployeeNotFound
        deleteEmployeeFromDataStore(id)
        publishEvent(EmployeeDeletedEvent(employee))
        return SuccessfullyDeleted
    }

}

sealed class DeleteEmployeeByIdResult {
    object EmployeeNotFound : DeleteEmployeeByIdResult()
    object SuccessfullyDeleted : DeleteEmployeeByIdResult()
}

package skillmanagement.domain.employees.usecases.update

import skillmanagement.common.events.PublishEvent
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.model.EmployeeUpdatedEvent
import skillmanagement.domain.employees.usecases.get.GetEmployeeById
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotChanged
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotFound
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.SuccessfullyUpdatedEmployee
import java.util.UUID

@BusinessFunction
class UpdateEmployeeById(
    private val getEmployeeById: GetEmployeeById,
    private val updateEmployeeInDataStore: UpdateEmployeeInDataStore,
    private val publishEvent: PublishEvent
) {

    // TODO: Security - Who can change Employees?
    @RetryOnConcurrentEmployeeUpdate
    operator fun invoke(employeeId: UUID, block: (Employee) -> Employee): UpdateEmployeeByIdResult {
        val currentEmployee = getEmployeeById(employeeId) ?: return NotUpdatedBecauseEmployeeNotFound
        val modifiedEmployee = block(currentEmployee)

        if (currentEmployee == modifiedEmployee) return NotUpdatedBecauseEmployeeNotChanged(currentEmployee)
        assertNoInvalidModifications(currentEmployee, modifiedEmployee)

        val updatedEmployee = updateEmployeeInDataStore(modifiedEmployee)
        publishEvent(EmployeeUpdatedEvent(updatedEmployee))
        return SuccessfullyUpdatedEmployee(updatedEmployee)
    }

    private fun assertNoInvalidModifications(currentEmployee: Employee, modifiedEmployee: Employee) {
        check(currentEmployee.id == modifiedEmployee.id) { "ID must not be changed!" }
        check(currentEmployee.version == modifiedEmployee.version) { "Version must not be changed!" }
        check(currentEmployee.lastUpdate == modifiedEmployee.lastUpdate) { "Last update must not be changed!" }
    }

}

sealed class UpdateEmployeeByIdResult {
    object NotUpdatedBecauseEmployeeNotFound : UpdateEmployeeByIdResult()
    data class NotUpdatedBecauseEmployeeNotChanged(val employee: Employee) : UpdateEmployeeByIdResult()
    data class SuccessfullyUpdatedEmployee(val employee: Employee) : UpdateEmployeeByIdResult()
}

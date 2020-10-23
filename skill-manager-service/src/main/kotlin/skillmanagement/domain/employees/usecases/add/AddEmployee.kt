package skillmanagement.domain.employees.usecases.add

import org.springframework.util.IdGenerator
import skillmanagement.common.events.PublishEvent
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.EmailAddress
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.model.EmployeeAddedEvent
import skillmanagement.domain.employees.model.FirstName
import skillmanagement.domain.employees.model.JobTitle
import skillmanagement.domain.employees.model.LastName
import skillmanagement.domain.employees.model.TelephoneNumber
import java.time.Clock

@BusinessFunction
class AddEmployee(
    private val idGenerator: IdGenerator,
    private val insertEmployeeIntoDataStore: InsertEmployeeIntoDataStore,
    private val publishEvent: PublishEvent,
    private val clock: Clock
) {

    // TODO: Security - Only invokable by Employee-Admins
    operator fun invoke(
        firstName: FirstName,
        lastName: LastName,
        title: JobTitle,
        email: EmailAddress,
        telephone: TelephoneNumber
    ): Employee {
        val employee = Employee(
            id = idGenerator.generateId(),
            version = 1,
            firstName = firstName,
            lastName = lastName,
            title = title,
            email = email,
            telephone = telephone,
            lastUpdate = clock.instant()
        )
        insertEmployeeIntoDataStore(employee)
        publishEvent(EmployeeAddedEvent(employee))
        return employee
    }

}

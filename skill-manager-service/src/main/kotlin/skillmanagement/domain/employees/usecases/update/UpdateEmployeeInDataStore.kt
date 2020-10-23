package skillmanagement.domain.employees.usecases.update

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.domain.employees.model.Employee
import java.time.Clock

@TechnicalFunction
class UpdateEmployeeInDataStore(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val clock: Clock
) {

    private val statement = """
        UPDATE employees
        SET version = :version, data = :data
        WHERE id = :id AND version = :expectedVersion
        """

    /**
     * Updates the given [Employee] in the data store.
     *
     * This also updates the [Employee.version] and [Employee.lastUpdate]
     * properties. Always use the returned [Employee] instance for subsequent
     * operation!
     *
     * @throws ConcurrentEmployeeUpdateException in case there was a concurrent
     * update to this employee's data while the invoking operation was working.
     */
    operator fun invoke(employee: Employee): Employee = doUpdateEmployee(
        employee = employee.copy(version = employee.version + 1, lastUpdate = clock.instant()),
        expectedVersion = employee.version
    )

    private fun doUpdateEmployee(employee: Employee, expectedVersion: Int): Employee {
        val parameters = mapOf(
            "id" to employee.id.toString(),
            "version" to employee.version,
            "data" to objectMapper.writeValueAsString(employee),
            "expectedVersion" to expectedVersion
        )
        if (jdbcTemplate.update(statement, parameters) == 0) throw ConcurrentEmployeeUpdateException()
        return employee
    }

}

class ConcurrentEmployeeUpdateException : RuntimeException()

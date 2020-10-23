package skillmanagement.domain.employees.usecases.add

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.domain.employees.model.Employee

@TechnicalFunction
class InsertEmployeeIntoDataStore(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper
) {

    private val statement = """
        INSERT INTO employees (id, version, data)
        VALUES (:id, :version, :data)
        """

    operator fun invoke(employee: Employee) {
        val parameters = mapOf(
            "id" to employee.id.toString(),
            "version" to employee.version,
            "data" to objectMapper.writeValueAsString(employee)
        )
        jdbcTemplate.update(statement, parameters)
    }

}

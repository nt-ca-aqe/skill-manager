package skillmanagement.domain.employees.usecases.delete

import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.domain.employees.model.EmployeeId

@TechnicalFunction
internal class DeleteEmployeeFromDataStoreFunction(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    private val singleStatement = "DELETE FROM employees WHERE id = :id"
    private val allStatement = "DELETE FROM employees"

    operator fun invoke(id: EmployeeId) {
        jdbcTemplate.update(singleStatement, mapOf("id" to "$id"))
    }

    operator fun invoke() {
        jdbcTemplate.update(allStatement, EmptySqlParameterSource.INSTANCE)
    }

}

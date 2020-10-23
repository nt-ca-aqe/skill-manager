package skillmanagement.domain.employees.usecases.get

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.domain.employees.model.Employee
import java.sql.ResultSet
import java.util.UUID

@TechnicalFunction
class GetEmployeesFromDataStore(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    objectMapper: ObjectMapper
) {

    private val singleIdQuery = "SELECT id, data FROM employees WHERE id = :id"
    private val multipleIdsQuery = "SELECT id, data FROM employees WHERE id IN (:ids)"
    private val allQuery = "SELECT id, data FROM employees"

    private val rowMapper = EmployeeRowMapper(objectMapper)

    operator fun invoke(id: UUID): Employee? =
        jdbcTemplate.query(singleIdQuery, mapOf("id" to "$id"), rowMapper).singleOrNull()

    operator fun invoke(ids: Collection<UUID>, chunkSize: Int = 1_000): Map<UUID, Employee> = ids.asSequence()
        .map(UUID::toString)
        .chunked(chunkSize)
        .map { jdbcTemplate.query(multipleIdsQuery, mapOf("ids" to it), rowMapper).filterNotNull() }
        .flatten()
        .map { it.id to it }
        .toMap()

    operator fun invoke(callback: (Employee) -> Unit) {
        jdbcTemplate.query(allQuery) { rs -> rowMapper.mapRow(rs, -1)?.also(callback) }
    }

}

private class EmployeeRowMapper(private val objectMapper: ObjectMapper) : RowMapper<Employee?> {

    private val log = KotlinLogging.logger {}

    override fun mapRow(rs: ResultSet, rowNum: Int): Employee? = tryToDeserialize(rs.data, rs.id)

    private fun tryToDeserialize(data: String, id: UUID): Employee? = try {
        objectMapper.readValue<Employee>(data)
    } catch (e: JsonProcessingException) {
        log.error(e) { "Could not read data of employee [$id]: ${e.message}" }
        log.debug { "Corrupted data: $data" }
        null
    }

    private val ResultSet.id: UUID
        get() = UUID.fromString(getString("id"))
    private val ResultSet.data: String
        get() = getString("data")

}

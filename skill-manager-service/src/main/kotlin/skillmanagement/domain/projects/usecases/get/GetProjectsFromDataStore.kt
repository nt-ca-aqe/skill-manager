package skillmanagement.domain.projects.usecases.get

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging.logger
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.domain.projects.model.Project
import java.sql.ResultSet
import java.util.UUID

@TechnicalFunction
class GetProjectsFromDataStore(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    objectMapper: ObjectMapper
) {

    private val rowMapper = ProjectRowMapper(objectMapper)

    private val singleIdQuery = "SELECT id, data FROM projects WHERE id = :id"
    private val multipleIdsQuery = "SELECT id, data FROM projects WHERE id IN (:ids)"
    private val allQuery = "SELECT id, data FROM projects"

    operator fun invoke(id: UUID): Project? =
        jdbcTemplate.query(singleIdQuery, mapOf("id" to "$id"), rowMapper).firstOrNull()

    operator fun invoke(ids: Collection<UUID>, chunkSize: Int = 1_000): Map<UUID, Project> = ids.asSequence()
        .map(UUID::toString)
        .chunked(chunkSize)
        .map { jdbcTemplate.query(multipleIdsQuery, mapOf("ids" to it), rowMapper).filterNotNull() }
        .flatten()
        .map { it.id to it }
        .toMap()

    operator fun invoke(callback: (Project) -> Unit) {
        jdbcTemplate.query(allQuery) { rs -> rowMapper.mapRow(rs, -1)?.also(callback) }
    }

}

private class ProjectRowMapper(private val objectMapper: ObjectMapper) : RowMapper<Project?> {

    private val log = logger {}

    override fun mapRow(rs: ResultSet, rowNum: Int): Project? = tryToDeserialize(rs.data, rs.id)

    private fun tryToDeserialize(data: String, id: UUID): Project? = try {
        objectMapper.readValue<Project>(data)
    } catch (e: JsonProcessingException) {
        log.error(e) { "Could not read data of project [$id]: ${e.message}" }
        log.debug { "Corrupted data: $data" }
        null
    }

    private val ResultSet.id: UUID
        get() = UUID.fromString(getString("id"))
    private val ResultSet.data: String
        get() = getString("data")

}

package skillmanagement.domain.projects.usecases.read

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging.logger
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.domain.projects.model.ProjectEntity
import skillmanagement.domain.projects.model.ProjectId
import skillmanagement.domain.projects.model.projectId
import java.sql.ResultSet

@TechnicalFunction
internal class GetProjectsFromDataStoreFunction(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    objectMapper: ObjectMapper
) {

    private val rowMapper = ProjectRowMapper(objectMapper)

    private val singleIdQuery = "SELECT id, data FROM projects WHERE id = :id"
    private val multipleIdsQuery = "SELECT id, data FROM projects WHERE id IN (:ids)"
    private val allQuery = "SELECT id, data FROM projects"

    operator fun invoke(id: ProjectId): ProjectEntity? =
        jdbcTemplate.query(singleIdQuery, mapOf("id" to "$id"), rowMapper).firstOrNull()

    operator fun invoke(ids: Collection<ProjectId>, chunkSize: Int = 1_000): Map<ProjectId, ProjectEntity> = ids
        .asSequence()
        .map(ProjectId::toString)
        .chunked(chunkSize)
        .map { jdbcTemplate.query(multipleIdsQuery, mapOf("ids" to it), rowMapper).filterNotNull() }
        .flatten()
        .map { it.id to it }
        .toMap()

    operator fun invoke(callback: (ProjectEntity) -> Unit) {
        jdbcTemplate.query(allQuery) { rs -> rowMapper.mapRow(rs, -1)?.also(callback) }
    }

}

internal class ProjectRowMapper(private val objectMapper: ObjectMapper) : RowMapper<ProjectEntity?> {

    private val log = logger {}

    override fun mapRow(rs: ResultSet, rowNum: Int): ProjectEntity? = tryToDeserialize(rs.data, rs.id)

    private fun tryToDeserialize(data: String, id: ProjectId): ProjectEntity? = try {
        objectMapper.readValue<ProjectEntity>(data)
    } catch (e: JsonProcessingException) {
        log.error(e) { "Could not read data of project [$id]: ${e.message}" }
        log.debug { "Corrupted data: $data" }
        null
    }

    private val ResultSet.id: ProjectId
        get() = projectId(getString("id"))
    private val ResultSet.data: String
        get() = getString("data")

}

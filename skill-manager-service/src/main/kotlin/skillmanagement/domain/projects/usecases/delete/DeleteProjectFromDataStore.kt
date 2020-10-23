package skillmanagement.domain.projects.usecases.delete

import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import java.util.UUID

@TechnicalFunction
class DeleteProjectFromDataStore(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    private val singleStatement = "DELETE FROM projects WHERE id = :id"
    private val allStatement = "DELETE FROM projects"

    operator fun invoke(id: UUID) {
        jdbcTemplate.update(singleStatement, mapOf("id" to "$id"))
    }

    operator fun invoke() {
        jdbcTemplate.update(allStatement, EmptySqlParameterSource.INSTANCE)
    }

}

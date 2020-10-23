package skillmanagement.domain.skills.usecases.delete

import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import java.util.UUID

@TechnicalFunction
class DeleteSkillFromDataStore(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    private val singleStatement = "DELETE FROM skills WHERE id = :id"
    private val allStatement = "DELETE FROM skills"

    operator fun invoke(id: UUID) {
        jdbcTemplate.update(singleStatement, mapOf("id" to "$id"))
    }

    operator fun invoke() {
        jdbcTemplate.update(allStatement, EmptySqlParameterSource.INSTANCE)
    }

}

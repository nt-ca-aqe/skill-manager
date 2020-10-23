package skillmanagement.domain.skills.usecases.add

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.domain.skills.model.Skill


@TechnicalFunction
class InsertSkillIntoDataStore(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper
) {

    private val statement = "INSERT INTO skills (id, version, data) VALUES (:id, :version, :data)"

    operator fun invoke(skill: Skill) {
        val parameters = mapOf(
            "id" to skill.id.toString(),
            "version" to skill.version,
            "data" to objectMapper.writeValueAsString(skill)
        )
        jdbcTemplate.update(statement, parameters)
    }

}

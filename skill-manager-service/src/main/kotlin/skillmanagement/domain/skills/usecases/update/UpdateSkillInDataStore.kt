package skillmanagement.domain.skills.usecases.update

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.domain.skills.model.Skill
import java.time.Clock

@TechnicalFunction
class UpdateSkillInDataStore(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val clock: Clock
) {

    private val statement = """
        UPDATE skills
        SET version = :version, data = :data
        WHERE id = :id AND version = :expectedVersion
        """

    /**
     * Updates the given [Skill] in the data store.
     *
     * This also updates the [Skill.version] and [Skill.lastUpdate]
     * properties. Always use the returned [Skill] instance for subsequent
     * operation!
     *
     * @throws ConcurrentSkillUpdateException in case there was a concurrent
     * update to this skill's data while the invoking operation was working.
     */
    operator fun invoke(skill: Skill): Skill = doUpdateSkill(
        skill = skill.copy(version = skill.version + 1, lastUpdate = clock.instant()),
        expectedVersion = skill.version
    )

    private fun doUpdateSkill(skill: Skill, expectedVersion: Int): Skill {
        val parameters = mapOf(
            "id" to skill.id.toString(),
            "version" to skill.version,
            "data" to objectMapper.writeValueAsString(skill),
            "expectedVersion" to expectedVersion
        )
        if (jdbcTemplate.update(statement, parameters) == 0) throw ConcurrentSkillUpdateException()
        return skill
    }

}

class ConcurrentSkillUpdateException : RuntimeException()

package skillmanagement.domain.skills.usecases.update

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.domain.skills.model.skill_java
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.domain.skills.model.skill_python
import skillmanagement.domain.skills.usecases.add.InsertSkillIntoDataStore
import skillmanagement.domain.skills.usecases.get.GetSkillsFromDataStore
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.fixedClock

@JdbcTest
@AutoConfigureJson
@TestInstance(PER_CLASS)
@TechnologyIntegrationTest
internal class UpdateSkillInDataStoreTests(
    @Autowired val jdbcTemplate: NamedParameterJdbcTemplate,
    @Autowired val objectMapper: ObjectMapper
) {

    val clock = fixedClock("2020-07-16T12:34:56.789Z")

    val getSkill = GetSkillsFromDataStore(jdbcTemplate, objectMapper)
    val insertSkillIntoDataStore = InsertSkillIntoDataStore(jdbcTemplate, objectMapper)
    val updateSkillInDataStore = UpdateSkillInDataStore(jdbcTemplate, objectMapper, clock)

    @Test
    fun `updating an existing skill actually persists it`() {
        insertSkillIntoDataStore(skill_kotlin)
        val updatedSkill = updateSkillInDataStore(skill_kotlin)
        val loadedSkill = getSkill(skill_kotlin.id)
        assertThat(updatedSkill).isEqualTo(loadedSkill)
    }

    @Test
    fun `updating an existing skill increments it's version and set the last update timestamp`() {
        insertSkillIntoDataStore(skill_kotlin)
        val updatedSkill = updateSkillInDataStore(skill_kotlin)
        assertThat(updatedSkill).isNotEqualTo(skill_kotlin)
        assertThat(updatedSkill.version).isEqualTo(2)
        assertThat(updatedSkill.lastUpdate).isEqualTo(clock.instant())
    }

    /**
     * An existence check before executing the update would prevent this,
     * but also add another round trip to the database...
     **/
    @Test
    fun `updating a non-existing skill fails with slightly wrong exception`() {
        assertThrows<ConcurrentSkillUpdateException> {
            updateSkillInDataStore(skill_java)
        }
    }

    /**
     * An existence check before executing the update would prevent this,
     * but also add another round trip to the database...
     **/
    @Test
    fun `updating an existing skill based on data from an old version throws exception`() {
        insertSkillIntoDataStore(skill_python.copy(version = 42))
        assertThrows<ConcurrentSkillUpdateException> {
            updateSkillInDataStore(skill_python.copy(version = 41))
        }
    }

}

package skillmanagement.domain.skills.usecases.get

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.shouldBe
import io.mockk.called
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.domain.skills.model.skill_python
import skillmanagement.domain.skills.usecases.add.InsertSkillIntoDataStore
import skillmanagement.domain.skills.usecases.delete.DeleteSkillFromDataStore
import skillmanagement.test.ResetMocksAfterEachTest
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.uuid
import java.util.UUID

@JdbcTest
@AutoConfigureJson
@TestInstance(PER_CLASS)
@TechnologyIntegrationTest
internal class GetSkillsFromDataStoreTests(
    @Autowired val jdbcTemplate: NamedParameterJdbcTemplate,
    @Autowired val objectMapper: ObjectMapper
) {

    val insertSkillIntoDataStore = InsertSkillIntoDataStore(jdbcTemplate, objectMapper)
    val deleteSkillFromDataStore = DeleteSkillFromDataStore(jdbcTemplate)
    val getSkillsFromDataStore = GetSkillsFromDataStore(jdbcTemplate, objectMapper)

    @AfterEach
    fun deleteSkills() {
        deleteSkillFromDataStore()
    }

    @Nested
    inner class SingleId {

        @Test
        fun `returns NULL if nothing found with given ID`() {
            getSingleSkill(uuid()) shouldBe null
        }

        @Test
        fun `returns Skill if found by its ID`() {
            insert(skill_kotlin)
            getSingleSkill(skill_kotlin.id) shouldBe skill_kotlin
        }

        private fun getSingleSkill(id: UUID) = getSkillsFromDataStore(id)

    }

    @Nested
    inner class MultipleIds {

        @Test
        fun `returns empty Map for empty ID list`() {
            getMultipleSkills() shouldBe emptyMap()
        }

        @Test
        fun `returns empty map if none of the Skills were found`() {
            getMultipleSkills(uuid()) shouldBe emptyMap()
        }

        @Test
        fun `returns map with every found Skill`() {
            insert(skill_kotlin, skill_python)

            val actualSkills = getMultipleSkills(skill_kotlin.id, uuid(), skill_python.id)
            val expectedSkills = setOf(skill_kotlin, skill_python).map { it.id to it }.toMap()

            actualSkills shouldBe expectedSkills
        }

        private fun getMultipleSkills(vararg ids: UUID) = getSkillsFromDataStore(ids.toList(), chunkSize = 2)

    }

    @Nested
    @ResetMocksAfterEachTest
    inner class AllWithCallback {

        private val callback: (Skill) -> Unit = mockk(relaxed = true)

        @Test
        fun `callback is never invoked if there are no skills`() {
            execute()
            verify { callback wasNot called }
        }

        @Test
        fun `callback is invoked for each existing skill`() {
            insert(skill_kotlin, skill_python)

            execute()

            verify {
                callback(skill_kotlin)
                callback(skill_python)
            }
            confirmVerified(callback)
        }

        private fun execute() = getSkillsFromDataStore(callback)

    }

    @Nested
    inner class Deserialization {

        val skill = skill_kotlin
        val skillId = skill.id

        @Test
        @RecordLoggers(SkillRowMapper::class)
        fun `deserialization errors are logged but don't throw an exception`(log: LogRecord) {
            insert(skill)

            assertThat(getSkillsFromDataStore(skillId)).isNotNull()
            corruptData(skillId)
            assertThat(getSkillsFromDataStore(skillId)).isNull()

            val messages = log.messages
            assertThat(messages).hasSize(2)
            assertThat(messages[0]).startsWith("Could not read data of skill [$skillId]: Instantiation of")
            assertThat(messages[1]).startsWith("Corrupted data: {}")
        }

        private fun corruptData(skillId: UUID) {
            jdbcTemplate.update("UPDATE skills SET data = '{}' WHERE id = :id", mapOf("id" to "$skillId"))
        }

    }

    private fun insert(vararg skills: Skill) = skills.forEach { insertSkillIntoDataStore(it) }

}

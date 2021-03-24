package skillmanagement.domain.skills.usecases.delete

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.domain.skills.usecases.create.InsertSkillIntoDataStoreFunction
import skillmanagement.domain.skills.usecases.read.GetSkillsFromDataStoreFunction
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.uuid

@JdbcTest
@AutoConfigureJson
@TestInstance(PER_CLASS)
@TechnologyIntegrationTest
internal class DeleteSkillFromDataStoreFunctionTests(
    @Autowired val jdbcTemplate: NamedParameterJdbcTemplate,
    @Autowired val objectMapper: ObjectMapper
) {

    private val getSkill = GetSkillsFromDataStoreFunction(jdbcTemplate, objectMapper)
    private val insertSkillIntoDataStore = InsertSkillIntoDataStoreFunction(jdbcTemplate, objectMapper)

    private val deleteSkillFromDataStore = DeleteSkillFromDataStoreFunction(jdbcTemplate)

    @Test
    fun `does not fail if skill with id does not exist`() {
        val id = uuid()
        getSkill(id) shouldBe null
        deleteSkillFromDataStore(id)
    }

    @Test
    fun `deletes existing skill from data store`() {
        val skill = skill_kotlin
        val id = skill.id

        getSkill(id) shouldBe null
        insertSkillIntoDataStore(skill)
        getSkill(id) shouldBe skill
        deleteSkillFromDataStore(id)
        getSkill(id) shouldBe null
    }

}

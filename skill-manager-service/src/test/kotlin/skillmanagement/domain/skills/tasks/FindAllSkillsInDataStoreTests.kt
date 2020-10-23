package skillmanagement.domain.skills.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.domain.skills.model.skill_python
import skillmanagement.domain.skills.usecases.add.InsertSkillIntoDataStore
import skillmanagement.domain.skills.usecases.get.GetSkillsFromDataStore
import skillmanagement.test.TechnologyIntegrationTest

@JdbcTest
@AutoConfigureJson
@TestInstance(PER_CLASS)
@TechnologyIntegrationTest
internal class FindAllSkillsInDataStoreTests(
    @Autowired val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    @Autowired val objectMapper: ObjectMapper
) {

    val insertSkillIntoDataStore = InsertSkillIntoDataStore(namedParameterJdbcTemplate, objectMapper)
    val getSkillsFromDataStore = GetSkillsFromDataStore(namedParameterJdbcTemplate, objectMapper)

    @Test
    fun `returns empty list if there are no Skills`() {
        findSkills() shouldBe emptySet()
    }

    @Test
    fun `returns single entry list if there is only one Skill`() {
        insertSkillIntoDataStore(skill_kotlin)
        findSkills() shouldBe setOf(skill_kotlin)
    }

    @Test
    fun `returns multi entry list if there are multiple Skills`() {
        insertSkillIntoDataStore(skill_kotlin)
        insertSkillIntoDataStore(skill_python)
        findSkills() shouldBe setOf(skill_kotlin, skill_python)
    }

    fun findSkills(): Set<Skill> {
        val foundSkills = mutableSetOf<Skill>()
        getSkillsFromDataStore { foundSkills.add(it) }
        return foundSkills
    }

}

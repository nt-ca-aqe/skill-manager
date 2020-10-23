package skillmanagement.domain.skills.usecases.get

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.test.UnitTest

@UnitTest
internal class GetSkillByIdTests {

    val id = skill_kotlin.id

    val getSkillsFromDataStore: GetSkillsFromDataStore = mockk()
    val getSkillById = GetSkillById(getSkillsFromDataStore)

    @Test
    fun `returns NULL if nothing found with given ID`() {
        every { getSkillsFromDataStore(id) } returns null
        getSkillById(id) shouldBe null
    }

    @Test
    fun `returns Skill if found by its ID`() {
        every { getSkillsFromDataStore(id) } returns skill_kotlin
        getSkillById(id) shouldBe skill_kotlin
    }

}

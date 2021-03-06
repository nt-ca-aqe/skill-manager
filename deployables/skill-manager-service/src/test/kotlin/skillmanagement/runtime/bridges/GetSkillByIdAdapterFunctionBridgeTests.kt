package skillmanagement.runtime.bridges

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.model.SkillDescription
import skillmanagement.domain.skills.model.SkillEntity
import skillmanagement.domain.skills.model.SkillLabel
import skillmanagement.domain.skills.model.Tag
import skillmanagement.domain.skills.model.skillId
import skillmanagement.domain.skills.usecases.read.GetSkillByIdFunction
import skillmanagement.test.ResetMocksAfterEachTest
import skillmanagement.test.UnitTest
import skillmanagement.test.instant

@UnitTest
@ResetMocksAfterEachTest
internal class GetSkillByIdAdapterFunctionBridgeTests {

    private val getSkillById: GetSkillByIdFunction = mockk()
    private val getSkillByIdAdapter = GetSkillByIdAdapterFunctionBridge(getSkillById)

    @Test
    fun `delegates to function of other module and converts the result`() {
        val skill = SkillEntity(
            id = skillId("3f7985b9-f5f0-4662-bda9-1dcde01f5f3b"),
            version = 1,
            data = Skill(
                label = SkillLabel("Kotlin"),
                description = SkillDescription("The coolest programming language."),
                tags = sortedSetOf(Tag("language"), Tag("cool"))
            ),
            created = instant("2020-07-14T12:34:56.789Z"),
            lastUpdate = instant("2020-07-14T12:34:56.789Z")
        )
        every { getSkillById(skill.id) } returns skill

        getSkillByIdAdapter(skillId(skill.id)) shouldBe skill_data_kotlin
    }

    @Test
    fun `returns null if there is no project with thte given ID`() {
        every { getSkillById(any()) } returns null
        getSkillByIdAdapter(externalSkillId()) shouldBe null
    }

}

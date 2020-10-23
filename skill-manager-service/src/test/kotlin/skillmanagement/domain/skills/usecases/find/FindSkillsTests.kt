package skillmanagement.domain.skills.usecases.find

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import skillmanagement.common.search.pageOf
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.domain.skills.model.skill_python
import skillmanagement.domain.skills.searchindex.SkillSearchIndex
import skillmanagement.domain.skills.usecases.get.GetSkillsFromDataStore
import skillmanagement.test.UnitTest

@UnitTest
internal class FindSkillsTests {

    val getSkillsFromDataStore: GetSkillsFromDataStore = mockk()
    val searchIndex: SkillSearchIndex = mockk()
    val findSkills = FindSkills(getSkillsFromDataStore, searchIndex)

    @Test
    fun `AllSkillsQuery just returns all skills from database`() {
        val query = AllSkillsQuery()
        val ids = listOf(skill_kotlin.id, skill_python.id)
        every { searchIndex.findAll(query) } returns pageOf(ids)
        every { getSkillsFromDataStore(ids) } returns skillMap(skill_python, skill_kotlin)

        findSkills(query) shouldBe pageOf(listOf(skill_kotlin, skill_python))
    }

    @Test
    fun `SkillsMatchingQuery gets IDs from search index and then corresponding skills from database`() {
        val query = SkillsMatchingQuery(queryString = "kotlin")
        val ids = listOf(skill_kotlin.id)
        every { searchIndex.query(query) } returns pageOf(ids)
        every { getSkillsFromDataStore(ids) } returns skillMap(skill_kotlin)

        findSkills(query) shouldBe pageOf(listOf(skill_kotlin))
    }

    fun skillMap(vararg skills: Skill) = skills.map { it.id to it }.toMap()

}

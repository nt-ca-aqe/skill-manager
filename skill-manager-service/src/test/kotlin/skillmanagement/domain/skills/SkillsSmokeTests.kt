package skillmanagement.domain.skills

import io.mockk.InternalPlatformDsl.toStr
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.hateoas.RepresentationModel
import skillmanagement.domain.skills.model.SkillDescription
import skillmanagement.domain.skills.model.SkillLabel
import skillmanagement.domain.skills.model.SkillResource
import skillmanagement.domain.skills.model.Tag
import skillmanagement.domain.skills.searchindex.SkillSearchIndex
import skillmanagement.domain.skills.usecases.delete.DeleteSkillFromDataStore
import skillmanagement.test.SmokeTest
import skillmanagement.test.SpringBootTestWithDockerizedDependencies
import java.lang.Thread.sleep

@SmokeTest
@SpringBootTestWithDockerizedDependencies
class SkillsSmokeTests(
    @Autowired val deleteSkillsFromDataStore: DeleteSkillFromDataStore,
    @Autowired val searchIndex: SkillSearchIndex,
    @LocalServerPort val port: Int
) {

    val skills = SkillsTestDriver(port = port)

    @AfterEach
    fun cleanUp() {
        deleteSkillsFromDataStore()
        searchIndex.reset()
    }

    @Nested
    inner class CrudOperations {

        @Test
        fun `skills can be added`() {
            val actual = skills.add(
                label = "Kotlin",
                description = "The coolest language on the JVM!",
                tags = setOf("language", "cool")
            )

            val expected = SkillResource(
                id = actual.id,
                label = SkillLabel("Kotlin"),
                description = SkillDescription("The coolest language on the JVM!"),
                tags = sortedSetOf(Tag("language"), Tag("cool"))
            )

            assertThat(actual).isEqualTo(expected)
            assertThat(linkNames(actual)).containsOnly("self", "delete")
        }

        @Test
        fun `skills can be got by their ID`() {
            val addedResource = skills.add(
                label = "Python",
                description = "Not just a snake...",
                tags = setOf("language")
            )
            val gotResource = skills.get(addedResource.id)

            assertThat(gotResource).isEqualTo(addedResource)
            assertThat(linkNames(gotResource)).containsOnly("self", "delete")
        }

        @Test
        fun `skills can be deleted by their ID`() {
            val skillId = skills.add().id

            assertThat(skills.get(skillId)).isNotNull()
            skills.delete(skillId)
            assertThat(skills.get(skillId)).isNull()
        }

    }

    @Nested
    inner class FindingAllSkills {

        @Test
        fun `getting all skills returns a paged result`() {
            val python = skills.add(label = "Python")
            val kotlin = skills.add(
                label = "Kotlin",
                description = "The coolest language on the JVM!",
                tags = setOf("language", "cool")
            )

            waitUntilSearchIndexIsRefreshed()

            assertThat(getAll()).containsExactly(kotlin, python)
        }

        @Test
        fun `pagination works properly`() {
            val skill1 = skills.add(label = "Skill #1")
            val skill2 = skills.add(label = "Skill #2")
            val skill3 = skills.add(label = "Skill #3")
            val skill4 = skills.add(label = "Skill #4")
            val skill5 = skills.add(label = "Skill #5")

            waitUntilSearchIndexIsRefreshed()

            assertThat(getAll(page = 0, size = 3))
                .containsExactly(skill1, skill2, skill3)
            assertThat(getAll(page = 1, size = 3))
                .containsExactly(skill4, skill5)
        }

        private fun getAll(page: Int = 0, size: Int = 100) =
            skills.getAll(page = page, size = size).content

    }

    @Nested
    inner class SearchingForSkills {

        @Test
        fun `searching for skills returns a paged result`() {
            val python1 = skills.add(label = "Python #1")
            val python2 = skills.add(label = "Python #2")
            val kotlin1 = skills.add(label = "Kotlin #1")
            val kotlin2 = skills.add(
                label = "Kotlin #2",
                description = "The coolest language on the JVM!",
                tags = setOf("language", "cool")
            )

            waitUntilSearchIndexIsRefreshed()

            assertThat(search("python")).containsOnly(python1, python2)
            assertThat(search("#1")).containsOnly(kotlin1, python1)
            assertThat(search("tags:cool")).containsOnly(kotlin2)
        }

        @Test
        fun `pagination works properly`() {
            val skill1 = skills.add(label = "Skill #1")
            val skill2 = skills.add(label = "Skill #2")
            val skill3 = skills.add(label = "Skill #3")
            val skill4 = skills.add(label = "Skill #4")
            val skill5 = skills.add(label = "Skill #5")

            waitUntilSearchIndexIsRefreshed()

            val resultsFromPage1 = search(query = "skill", page = 0, size = 3)
            val resultsFromPage2 = search(query = "skill", page = 1, size = 3)

            assertThat(resultsFromPage1).hasSize(3)
            assertThat(resultsFromPage2).hasSize(2)
            assertThat(resultsFromPage1 + resultsFromPage2)
                .containsOnly(skill1, skill2, skill3, skill4, skill5)
        }

        private fun search(query: String, page: Int = 0, size: Int = 100) =
            skills.search(query = query, page = page, size = size).content

    }

    private fun waitUntilSearchIndexIsRefreshed() {
        searchIndex.refresh()
        sleep(1_500)
    }

    fun linkNames(model: RepresentationModel<*>?) =
        model?.getLinks()?.map { it.rel.toStr() }?.toSet()

}

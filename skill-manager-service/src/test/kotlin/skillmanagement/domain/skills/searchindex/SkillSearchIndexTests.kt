package skillmanagement.domain.skills.searchindex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import skillmanagement.common.model.Suggestion
import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.search.PagedFindAllQuery
import skillmanagement.common.search.PagedStringQuery
import skillmanagement.configuration.ElasticsearchProperties
import skillmanagement.configuration.createElasticsearchClient
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.model.skill
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.docker.ElasticsearchContainer
import skillmanagement.test.docker.RunWithDockerizedElasticsearch
import java.util.UUID

@TechnologyIntegrationTest
@RunWithDockerizedElasticsearch
internal class SkillSearchIndexTests(
    container: ElasticsearchContainer
) {

    // TODO: test find all + pagination

    val properties = ElasticsearchProperties(port = container.getMappedPort())
    val cut = SkillSearchIndex(createElasticsearchClient(properties))

    val kotlin1 = skill(label = "Kotlin #1", tags = setOf("language", "cool"))
    val kotlin2 = skill(label = "Kotlin #2")
    val python = skill(label = "Python", tags = setOf("language", "scripting"))
    val java = skill(label = "Java", tags = setOf("language"))

    @BeforeEach
    fun reset() {
        cut.reset()
    }

    @Test
    fun `querying an empty index does not return any IDs`() {
        assertThat(query("foo")).isEmpty()
    }

    @Test
    fun `query can be used to search skill label and tags`() {
        index(kotlin1, python, java, kotlin2)

        assertThat(query("kotlin")).containsOnly(kotlin1.id, kotlin2.id)
        assertThat(query("label:*o*")).containsOnly(kotlin1.id, kotlin2.id, python.id)
        assertThat(query("tags:language")).containsOnly(kotlin1.id, python.id, java.id)
        assertThat(query("tags:cool")).containsOnly(kotlin1.id)
        assertThat(query("label:kotlin AND tags:language")).containsOnly(kotlin1.id)
    }

    @Test
    fun `query only uses label by default`() {
        index(kotlin1, python, java, kotlin2)

        assertThat(query("python")).containsOnly(python.id)
        assertThat(query("kotlin")).containsOnly(kotlin1.id, kotlin2.id)
        assertThat(query("language")).isEmpty()
    }

    @Test
    fun `entries can be deleted`() {
        index(kotlin1, kotlin2)
        assertThat(query("kotlin")).containsOnly(kotlin1.id, kotlin2.id)
        delete(kotlin1.id)
        assertThat(query("kotlin")).containsOnly(kotlin2.id)
    }

    @Test
    fun `existing skills can be suggested`() {
        val skill1 = skill(label = "The Kotlin")
        val skill2 = skill(label = "Kotlin #1")
        val skill3 = skill(label = "Kotlin #2")
        val skill4 = skill(label = "Python")
        index(skill1, skill2, skill3, skill4)

        assertThat(cut.suggestExisting("ko", 3))
            .containsOnly(
                suggestion(skill1),
                suggestion(skill2),
                suggestion(skill3)
            )
    }

    private fun index(vararg skills: Skill) {
        skills.forEach(cut::index)
        cut.refresh()
    }

    private fun delete(vararg ids: UUID) {
        ids.forEach(cut::deleteById)
        cut.refresh()
    }

    private fun query(query: String, pageIndex: PageIndex = PageIndex.DEFAULT, pageSize: PageSize = PageSize.DEFAULT) =
        cut.query(SimplePagedStringQuery(query, pageIndex, pageSize))

    private fun suggestion(skill2: Skill) =
        Suggestion(skill2.id, skill2.label.toString())

    private data class SimplePagedStringQuery(
        override val queryString: String,
        override val pageIndex: PageIndex,
        override val pageSize: PageSize
    ) : PagedStringQuery

    private data class SimplePagedFindAllQuery(
        override val pageIndex: PageIndex,
        override val pageSize: PageSize
    ) : PagedFindAllQuery
}

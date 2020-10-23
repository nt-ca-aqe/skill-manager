package skillmanagement.domain.skills

import org.springframework.hateoas.PagedModel
import skillmanagement.domain.skills.model.SkillDescription
import skillmanagement.domain.skills.model.SkillLabel
import skillmanagement.domain.skills.model.SkillResource
import skillmanagement.domain.skills.model.Tag
import skillmanagement.domain.skills.usecases.add.AddSkillHttpAdapter
import skillmanagement.domain.skills.usecases.find.SearchForSkillsHttpAdapter
import skillmanagement.test.AbstractHttpTestDriver
import java.util.Collections.emptySortedSet
import java.util.UUID

class SkillsTestDriver(
    host: String = "localhost",
    port: Int = 8080
) : AbstractHttpTestDriver(host, port) {

    fun add(
        label: String = "Dummy Skill",
        description: String? = null,
        tags: Set<String> = emptySortedSet()
    ): SkillResource {
        val response = post("/api/skills") {
            AddSkillHttpAdapter.Request(
                label = SkillLabel(label),
                description = description?.let(::SkillDescription),
                tags = tags.map(::Tag).toSortedSet()
            )
        }
        return when (response.code) {
            201 -> response.readBodyAs(SkillResource::class)
            else -> error(unmappedCase(response))
        }
    }

    fun get(id: UUID): SkillResource? {
        val response = get("/api/skills/$id")
        return when (response.code) {
            200 -> response.readBodyAs(SkillResource::class)
            204 -> null
            else -> error(unmappedCase(response))
        }
    }

    fun getAll(page: Int = 0, size: Int = 100): PagedModel<SkillResource> {
        val response = get("/api/skills?page=$page&size=$size")
        return when (response.code) {
            200 -> response.readBodyAs(SkillsPageModel::class)
            else -> error(unmappedCase(response))
        }
    }

    fun search(query: String, page: Int = 0, size: Int = 100): PagedModel<SkillResource> {
        val response = post("/api/skills/_search?page=$page&size=$size") {
            SearchForSkillsHttpAdapter.Request(query)
        }
        return when (response.code) {
            200 -> response.readBodyAs(SkillsPageModel::class)
            else -> error(unmappedCase(response))
        }
    }

    fun delete(id: UUID) {
        val response = delete("/api/skills/$id")
        if (response.code != 204) {
            error(unmappedCase(response))
        }
    }

    private class SkillsPageModel : PagedModel<SkillResource>()

}

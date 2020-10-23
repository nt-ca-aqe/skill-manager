package skillmanagement.domain.skills.usecases.find

import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.skills.model.SkillResource
import skillmanagement.domain.skills.model.toSearchResource

@HttpAdapter
@RequestMapping("/api/skills/_search")
class SearchForSkillsHttpAdapter(
    private val findSkills: FindSkills
) {

    @PostMapping
    fun post(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "100") size: Int,
        @RequestBody request: Request
    ): PagedModel<SkillResource> {
        val skills = findSkills(SkillsMatchingQuery(PageIndex(page), PageSize(size), request.query))
        return skills.toSearchResource()
    }

    data class Request(
        val query: String
    )

}

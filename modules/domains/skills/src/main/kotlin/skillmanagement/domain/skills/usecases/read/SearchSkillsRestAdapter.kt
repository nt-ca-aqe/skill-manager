package skillmanagement.domain.skills.usecases.read

import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.model.PageIndex
import skillmanagement.common.model.PageSize
import skillmanagement.common.model.Pagination
import skillmanagement.common.stereotypes.RestAdapter
import skillmanagement.domain.skills.model.SkillRepresentation
import skillmanagement.domain.skills.model.toSearchResource

@RestAdapter
@RequestMapping("/api/skills/_search")
internal class SearchSkillsRestAdapter(
    private val getSkillsPage: GetSkillsPageFunction
) {

    @PostMapping
    fun post(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestBody request: Request
    ): PagedModel<SkillRepresentation> {
        val skills = getSkillsPage(query(request.query, page, size))
        return skills.toSearchResource()
    }

    private fun query(query: String, page: Int?, size: Int?) =
        SkillsMatchingQuery(query, Pagination(PageIndex.of(page), PageSize.of(size)))

    data class Request(
        val query: String
    )

}

package skillmanagement.domain.skills.usecases.find

import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.skills.model.SkillResource
import skillmanagement.domain.skills.model.toAllResource

@HttpAdapter
@RequestMapping("/api/skills")
class FindAllSkillsHttpAdapter(
    private val findSkills: FindSkills
) {

    @GetMapping
    fun get(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "100") size: Int
    ): PagedModel<SkillResource> {
        val skills = findSkills(AllSkillsQuery(PageIndex(page), PageSize(size)))
        return skills.toAllResource()
    }

}

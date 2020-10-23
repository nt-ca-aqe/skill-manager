package skillmanagement.domain.skills.usecases.find

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.model.Suggestion
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.skills.searchindex.SkillSearchIndex

@HttpAdapter
@RequestMapping("/api/skills/_suggest")
class SuggestSkillsHttpAdapter(
    private val searchIndex: SkillSearchIndex
) {

    @PostMapping
    fun post(
        @RequestParam(defaultValue = "100") size: Int,
        @RequestBody request: Request
    ): List<Suggestion> =
        searchIndex.suggestExisting(input = request.input, size = size)

    data class Request(
        val input: String
    )

}

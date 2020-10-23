package skillmanagement.domain.skills.usecases.find

import skillmanagement.common.search.Page
import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.search.PagedFindAllQuery
import skillmanagement.common.search.PagedStringQuery
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.searchindex.SkillSearchIndex
import skillmanagement.domain.skills.usecases.get.GetSkillsFromDataStore

@BusinessFunction
class FindSkills(
    private val getSkillsFromDataStore: GetSkillsFromDataStore,
    private val searchIndex: SkillSearchIndex
) {

    // TODO: Security + query parameter + pagination
    operator fun invoke(query: FindSkillsQuery): Page<Skill> {
        val page = when (query) {
            is SkillsMatchingQuery -> searchIndex.query(query)
            is AllSkillsQuery -> searchIndex.findAll(query)
        }
        val skillsMap = getSkillsFromDataStore(page.content)
        val skills = page.content.mapNotNull { skillsMap[it] }
        return page.withOtherContent(skills)
    }

}

sealed class FindSkillsQuery

data class SkillsMatchingQuery(
    override val pageIndex: PageIndex = PageIndex.DEFAULT,
    override val pageSize: PageSize = PageSize.DEFAULT,
    override val queryString: String
) : PagedStringQuery, FindSkillsQuery()

data class AllSkillsQuery(
    override val pageIndex: PageIndex = PageIndex.DEFAULT,
    override val pageSize: PageSize = PageSize.DEFAULT
) : PagedFindAllQuery, FindSkillsQuery()

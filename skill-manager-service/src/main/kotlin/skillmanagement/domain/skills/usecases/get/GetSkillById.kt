package skillmanagement.domain.skills.usecases.get

import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.skills.model.Skill
import java.util.UUID

@BusinessFunction
class GetSkillById(
    private val getSkillsFromDataStore: GetSkillsFromDataStore
) {

    operator fun invoke(id: UUID): Skill? {
        return getSkillsFromDataStore(id)
    }

}

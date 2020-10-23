package skillmanagement.domain.skills.usecases.delete

import skillmanagement.common.events.PublishEvent
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.skills.model.SkillDeletedEvent
import skillmanagement.domain.skills.usecases.delete.DeleteSkillByIdResult.SkillNotFound
import skillmanagement.domain.skills.usecases.delete.DeleteSkillByIdResult.SuccessfullyDeleted
import skillmanagement.domain.skills.usecases.get.GetSkillById
import java.util.UUID

@BusinessFunction
class DeleteSkillById(
    private val getSkillById: GetSkillById,
    private val deleteSkillFromDataStore: DeleteSkillFromDataStore,
    private val publishEvent: PublishEvent
) {

    // TODO: Security - Only invokable by Skill-Admins
    operator fun invoke(id: UUID): DeleteSkillByIdResult {
        val skill = getSkillById(id) ?: return SkillNotFound
        deleteSkillFromDataStore(id)
        publishEvent(SkillDeletedEvent(skill))
        return SuccessfullyDeleted
    }

}

sealed class DeleteSkillByIdResult {
    object SkillNotFound : DeleteSkillByIdResult()
    object SuccessfullyDeleted : DeleteSkillByIdResult()
}

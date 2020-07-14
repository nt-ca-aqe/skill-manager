package skillmanagement.domain.skills.update

import skillmanagement.domain.BusinessFunction
import skillmanagement.domain.skills.Skill
import skillmanagement.domain.skills.get.GetSkillById
import skillmanagement.domain.skills.update.UpdateSkillByIdResult.SkillNotFound
import skillmanagement.domain.skills.update.UpdateSkillByIdResult.SuccessfullyUpdated
import java.util.UUID

@BusinessFunction
class UpdateSkillById(
    private val getSkillById: GetSkillById,
    private val updateSkillInDataStore: UpdateSkillInDataStore
) {

    // TODO: Security - Who can change Skills?
    @RetryOnConcurrentSkillUpdate
    operator fun invoke(skillId: UUID, block: (Skill) -> Skill): UpdateSkillByIdResult {
        val currentSkill = getSkillById(skillId) ?: return SkillNotFound
        val modifiedSkill = block(currentSkill)

        assertNoInvalidModifications(currentSkill, modifiedSkill)

        val updatedSkill = updateSkillInDataStore(modifiedSkill)
        return SuccessfullyUpdated(updatedSkill)
    }

    private fun assertNoInvalidModifications(currentSkill: Skill, modifiedSkill: Skill) {
        check(currentSkill.id == modifiedSkill.id) { "ID must not be changed!" }
        check(currentSkill.version == modifiedSkill.version) { "Version must not be changed!" }
        check(currentSkill.lastUpdate == modifiedSkill.lastUpdate) { "Last update must not be changed!" }
    }

}

sealed class UpdateSkillByIdResult {
    object SkillNotFound : UpdateSkillByIdResult()
    data class SuccessfullyUpdated(val skill: Skill) : UpdateSkillByIdResult()
}
package skillmanagement.domain.skills.usecases.update

import com.github.fge.jsonpatch.JsonPatch
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import skillmanagement.common.http.patch.ApplyPatch
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.skills.model.SkillChangeData
import skillmanagement.domain.skills.model.SkillResource
import skillmanagement.domain.skills.model.merge
import skillmanagement.domain.skills.model.toChangeData
import skillmanagement.domain.skills.model.toResource
import skillmanagement.domain.skills.usecases.update.UpdateSkillByIdResult.SkillNotFound
import skillmanagement.domain.skills.usecases.update.UpdateSkillByIdResult.SuccessfullyUpdated
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/skills/{skillId}")
class UpdateSkillByIdHttpAdapter(
    private val updateSkillById: UpdateSkillById,
    private val applyPatch: ApplyPatch
) {

    @PutMapping
    fun put(
        @PathVariable skillId: UUID,
        @RequestBody request: SkillChangeData
    ): ResponseEntity<SkillResource> {
        val result = updateSkillById(skillId) {
            it.merge(request)
        }
        return when (result) {
            SkillNotFound -> notFound().build()
            is SuccessfullyUpdated -> ok(result.skill.toResource())
        }
    }

    @PatchMapping(consumes = ["application/json-patch+json"])
    fun patch(
        @PathVariable skillId: UUID,
        @RequestBody patch: JsonPatch
    ): ResponseEntity<SkillResource> {
        val result = updateSkillById(skillId) {
            it.merge(applyPatch(patch, it.toChangeData()))
        }
        return when (result) {
            SkillNotFound -> notFound().build()
            is SuccessfullyUpdated -> ok(result.skill.toResource())
        }
    }

}

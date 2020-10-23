package skillmanagement.domain.employees.usecases.skillknowledge.update

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
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.SkillKnowledge
import skillmanagement.domain.employees.model.SkillKnowledgeChangeData
import skillmanagement.domain.employees.model.merge
import skillmanagement.domain.employees.model.toChangeData
import skillmanagement.domain.employees.model.toResource
import skillmanagement.domain.employees.usecases.skillknowledge.update.UpdateSkillKnowledgeResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.skillknowledge.update.UpdateSkillKnowledgeResult.SkillKnowledgeNotChanged
import skillmanagement.domain.employees.usecases.skillknowledge.update.UpdateSkillKnowledgeResult.SkillKnowledgeNotFound
import skillmanagement.domain.employees.usecases.skillknowledge.update.UpdateSkillKnowledgeResult.SuccessfullyUpdatedSkillKnowledge
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/employees/{employeeId}/skills/{skillId}")
class UpdateSkillKnowledgeByIdHttpAdapter(
    private val updateSkillKnowledgeById: UpdateSkillKnowledgeById,
    private val applyPatch: ApplyPatch
) {

    @PutMapping
    fun put(
        @PathVariable employeeId: UUID,
        @PathVariable skillId: UUID,
        @RequestBody request: SkillKnowledgeChangeData
    ): ResponseEntity<EmployeeResource> =
        handleUpdate(employeeId, skillId) { it.merge(request) }

    @PatchMapping(consumes = ["application/json-patch+json"])
    fun patch(
        @PathVariable employeeId: UUID,
        @PathVariable skillId: UUID,
        @RequestBody patch: JsonPatch
    ): ResponseEntity<EmployeeResource> =
        handleUpdate(employeeId, skillId) { it.merge(applyPatch(patch, it.toChangeData())) }

    private fun handleUpdate(
        employeeId: UUID,
        skillId: UUID,
        block: (SkillKnowledge) -> SkillKnowledge
    ): ResponseEntity<EmployeeResource> =
        when (val result = updateSkillKnowledgeById(employeeId, skillId, block)) {
            EmployeeNotFound, SkillKnowledgeNotFound -> notFound().build()
            is SkillKnowledgeNotChanged -> ok(result.employee.toResource())
            is SuccessfullyUpdatedSkillKnowledge -> ok(result.employee.toResource())
        }

}

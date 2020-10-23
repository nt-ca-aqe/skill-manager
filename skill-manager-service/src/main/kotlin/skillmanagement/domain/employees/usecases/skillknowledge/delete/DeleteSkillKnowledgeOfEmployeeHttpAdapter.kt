package skillmanagement.domain.employees.usecases.skillknowledge.delete

import mu.KotlinLogging.logger
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.toResource
import skillmanagement.domain.employees.usecases.skillknowledge.delete.DeleteSkillKnowledgeOfEmployeeResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.skillknowledge.delete.DeleteSkillKnowledgeOfEmployeeResult.SkillKnowledgeNotFound
import skillmanagement.domain.employees.usecases.skillknowledge.delete.DeleteSkillKnowledgeOfEmployeeResult.SuccessfullyDeletedSkillKnowledge
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/employees/{employeeId}/skills/{skillId}")
class DeleteSkillKnowledgeOfEmployeeHttpAdapter(
    private val deleteSkillKnowledgeOfEmployee: DeleteSkillKnowledgeOfEmployee
) {

    private val log = logger {}

    @DeleteMapping
    fun delete(@PathVariable employeeId: UUID, @PathVariable skillId: UUID): ResponseEntity<EmployeeResource> {
        log.info { "Deleting knowledge of skill [$skillId] of employee [$employeeId]" }
        val result = deleteSkillKnowledgeOfEmployee(employeeId, skillId)
        log.info { "Result: $result" }
        return when (result) {
            EmployeeNotFound, SkillKnowledgeNotFound -> noContent().build()
            is SuccessfullyDeletedSkillKnowledge -> ok(result.employee.toResource())
        }
    }

}

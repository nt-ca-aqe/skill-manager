package skillmanagement.domain.employees.usecases.skillknowledge.delete

import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.usecases.skillknowledge.delete.DeleteSkillKnowledgeOfEmployeeResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.skillknowledge.delete.DeleteSkillKnowledgeOfEmployeeResult.SkillKnowledgeNotFound
import skillmanagement.domain.employees.usecases.skillknowledge.delete.DeleteSkillKnowledgeOfEmployeeResult.SuccessfullyDeletedSkillKnowledge
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeById
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotChanged
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotFound
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.SuccessfullyUpdatedEmployee
import java.util.UUID

@BusinessFunction
class DeleteSkillKnowledgeOfEmployee(
    private val updateEmployeeById: UpdateEmployeeById
) {

    // TODO: Security - Only invokable by Employee themselves or Employee-Admins
    operator fun invoke(employeeId: UUID, skillId: UUID): DeleteSkillKnowledgeOfEmployeeResult {
        val updateResult = updateEmployeeById(employeeId) {
            it.removeSkillKnowledgeBySkillId(skillId)
        }
        return when (updateResult) {
            is NotUpdatedBecauseEmployeeNotFound -> EmployeeNotFound
            is NotUpdatedBecauseEmployeeNotChanged -> SkillKnowledgeNotFound
            is SuccessfullyUpdatedEmployee -> SuccessfullyDeletedSkillKnowledge(updateResult.employee)
        }
    }

}

sealed class DeleteSkillKnowledgeOfEmployeeResult {
    object EmployeeNotFound : DeleteSkillKnowledgeOfEmployeeResult()
    object SkillKnowledgeNotFound : DeleteSkillKnowledgeOfEmployeeResult()
    data class SuccessfullyDeletedSkillKnowledge(val employee: Employee) : DeleteSkillKnowledgeOfEmployeeResult()
}

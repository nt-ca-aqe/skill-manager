package skillmanagement.domain.employees.usecases.skillknowledge.set

import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.model.SkillKnowledge
import skillmanagement.domain.employees.model.SkillLevel
import skillmanagement.domain.employees.usecases.skillknowledge.set.SetSkillKnowledgeOfEmployeeResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.skillknowledge.set.SetSkillKnowledgeOfEmployeeResult.SkillNotFound
import skillmanagement.domain.employees.usecases.skillknowledge.set.SetSkillKnowledgeOfEmployeeResult.SuccessfullyAssigned
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeById
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotChanged
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotFound
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.SuccessfullyUpdatedEmployee
import skillmanagement.domain.skills.usecases.get.GetSkillById
import java.util.UUID

@BusinessFunction
class SetSkillKnowledgeOfEmployee(
    private val getSkillById: GetSkillById,
    private val updateEmployeeById: UpdateEmployeeById
) {

    // TODO: Security - Only invokable by Employee themselves or Employee-Admins
    operator fun invoke(
        employeeId: UUID,
        skillId: UUID,
        level: SkillLevel,
        secret: Boolean
    ): SetSkillKnowledgeOfEmployeeResult {
        val skill = getSkillById(skillId) ?: return SkillNotFound
        val updateResult = updateEmployeeById(employeeId) { employee ->
            val skillKnowledge = SkillKnowledge(
                skill = skill,
                level = level,
                secret = secret
            )
            employee.setSkillKnowledge(skillKnowledge)
        }

        return when (updateResult) {
            is NotUpdatedBecauseEmployeeNotFound -> EmployeeNotFound
            is NotUpdatedBecauseEmployeeNotChanged -> error("should not happen")
            is SuccessfullyUpdatedEmployee -> SuccessfullyAssigned(updateResult.employee)
        }
    }

    private fun Employee.setSkillKnowledge(skillKnowledge: SkillKnowledge): Employee =
        copy(skills = skills.filter { it.skill.id != skillKnowledge.skill.id } + skillKnowledge)

}

sealed class SetSkillKnowledgeOfEmployeeResult {
    object EmployeeNotFound : SetSkillKnowledgeOfEmployeeResult()
    object SkillNotFound : SetSkillKnowledgeOfEmployeeResult()
    data class SuccessfullyAssigned(val employee: Employee) : SetSkillKnowledgeOfEmployeeResult()
}

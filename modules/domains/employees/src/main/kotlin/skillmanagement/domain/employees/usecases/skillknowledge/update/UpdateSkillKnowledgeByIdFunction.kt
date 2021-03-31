package skillmanagement.domain.employees.usecases.skillknowledge.update

import arrow.core.Either
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.model.SkillKnowledge
import skillmanagement.domain.employees.usecases.skillknowledge.update.UpdateFailure.EmployeeNotFound
import skillmanagement.domain.employees.usecases.skillknowledge.update.UpdateFailure.SkillKnowledgeNotChanged
import skillmanagement.domain.employees.usecases.skillknowledge.update.UpdateFailure.SkillKnowledgeNotFound
import skillmanagement.domain.employees.usecases.update.EmployeeUpdateFailure
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdFunction
import java.util.UUID

@BusinessFunction
class UpdateSkillKnowledgeByIdFunction internal constructor(
    private val updateEmployeeById: UpdateEmployeeByIdFunction
) {

    operator fun invoke(
        employeeId: UUID,
        skillId: UUID,
        block: (SkillKnowledge) -> SkillKnowledge
    ): Either<UpdateFailure, Employee> {
        var knowledgeExists = false
        val updateResult = updateEmployeeById(employeeId) { employee ->
            val updatedSkills = employee.skills
                .map { skillKnowledge ->
                    if (skillKnowledge.skill.id == skillId) {
                        knowledgeExists = true
                        update(skillKnowledge, block)
                    } else {
                        skillKnowledge
                    }
                }
            employee.copy(skills = updatedSkills)
        }

        return updateResult.mapLeft { failure ->
            when (failure) {
                is EmployeeUpdateFailure.EmployeeNotFound -> EmployeeNotFound
                is EmployeeUpdateFailure.EmployeeNotChanged -> when (knowledgeExists) {
                    true -> SkillKnowledgeNotChanged(failure.employee)
                    false -> SkillKnowledgeNotFound
                }
            }
        }
    }

    private fun update(
        currentKnowledge: SkillKnowledge,
        block: (SkillKnowledge) -> SkillKnowledge
    ): SkillKnowledge {
        val modifiedKnowledge = block(currentKnowledge)
        check(currentKnowledge.skill == modifiedKnowledge.skill) { "Skill must not be changed!" }
        return modifiedKnowledge
    }

}

sealed class UpdateFailure {
    object EmployeeNotFound : UpdateFailure()
    object SkillKnowledgeNotFound : UpdateFailure()
    data class SkillKnowledgeNotChanged(val employee: Employee) : UpdateFailure()
}
